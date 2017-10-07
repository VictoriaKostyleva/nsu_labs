#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>
#include <unistd.h>//sleep, write

void* func(void *args) {

    while(1)
    write(0, "Something\n", 10);
    return NULL;
}

int main() {
    pthread_t thread;

    int status;

    status = pthread_create(&thread, NULL, func, NULL);
    if (status != 0) {
        char buf[256];
        strerror_r(status, buf, sizeof buf);
        printf("%s\n", buf);
        exit(1);
    }

    sleep(2);

    status = pthread_cancel(thread);
    if (status != 0) {
        char buf[256];
        strerror_r(status, buf, sizeof buf);
        printf("%s\n", buf);
        exit(1);
    }


    pthread_exit(NULL);

    return 0;
}

//compile g++ main.cpp -pthread
