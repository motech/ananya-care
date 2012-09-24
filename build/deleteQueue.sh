cookies_file=activemq.cookies;

usage(){
  echo "usage:"
  echo "sh deleteQueue.sh <activemq-host>:<activemq-port> <queue-name> <queue-name> ...";
  exit $1;
}

exit_script(){
  rm -f $cookies_file;
  echo $1;
  exit $2;
}

if [ $# -lt 1 ]; then
  usage 1;
fi


if [ $# -eq 1 ]; then
  exit_script "Nothing to delete." 0;
fi

host=$1;

response=$(curl -b $cookies_file -c $cookies_file -s -S "$host/admin/queues.jsp");

if [ "$?" -ne "0" ]; then
  exit_script "Error while connecting to activemq admin on $host." 1;
fi

delete(){
    echo "Deleting queue $1 ...";
    url=$(echo "$response" | grep -i \=$1\&.*secret.*delete | tail -1);

    if [ "$url" == "" ]; then
      echo "Error: Queue $1 not found."
    fi

    url=${url#*\"};
    url=${url%\"*};

    curl -b $cookies_file -c $cookies_file -s -S "$host/admin/$url";
    if [ "$?" -ne "0" ]; then
      echo "Error: Error while connecting to activemq admin on $host";
    fi

    echo "Queue $1 deleted.";
}

shift;

deleteAll() {
    for queue in "$@"; do
        delete $queue;
    done
}

for queues in "$@"; do
    deleteAll $queues;
done

exit_script "Done.";