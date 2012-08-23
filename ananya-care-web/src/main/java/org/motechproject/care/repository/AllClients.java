package org.motechproject.care.repository;

import org.motechproject.care.domain.Client;


public interface AllClients<T extends Client> {

    public T findByCaseId(String caseId);

    public void add(T client);

    public void update(T client);
}
