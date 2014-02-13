package org.motechproject.care.service;

import org.motechproject.care.domain.Client;
import org.motechproject.care.repository.AllClients;

public abstract class BaseService<T extends Client> {
    protected final VaccinationProcessor vaccinationProcessor;
    protected final AllClients<T> allClients;

    public BaseService(AllClients<T> allClients, VaccinationProcessor vaccinationProcessor) {
        this.vaccinationProcessor = vaccinationProcessor;
        this.allClients = allClients;
    }

    public void process(T client) {
        String caseId = client.getCaseId();
        synchronized (getLockName(caseId)) {
            onProcess(client);
        }
    }

    protected abstract void onProcess(T client);

    public boolean closeCase(String caseId) {
        synchronized (getLockName(caseId)) {
            T client = allClients.findByCaseId(caseId);
            if(client == null)
                return false;

            client.setClosedByCommcare(true);
            allClients.update(client);
            vaccinationProcessor.closeSchedules(client);
            return true;
        }
    }

    public boolean expireCase(String caseId) {
        synchronized (getLockName(caseId)) {
            T client = allClients.findByCaseId(caseId);
            if(client == null)
                return false;
            client.setExpired(true);
            allClients.update(client);
            vaccinationProcessor.closeSchedules(client);
            return true;
        }
    }

    private String getLockName(String caseId) {
        return String.format("%s-%s", BaseService.class.getCanonicalName(), caseId).intern();
    }
}
