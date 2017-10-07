#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>
#include <unistd.h>//sleep, write

void cancel_handler(void * arg) {
    printf("%s\n", (char*)arg);
}

void* func(void *args) {
    pthread_cleanup_push(cancel_handler, (void *)"Cancelled!!!");
    while(1)
    write(0, "Something\n", 10);
    pthread_cleanup_pop(0);
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
    printf("Trying to cancel\n");

    status = pthread_cancel(thread);
    if (status != 0) {
        char buf[256];
        strerror_r(status, buf, sizeof buf);
        printf("%s\n", buf);
        exit(1);
    }


    pthread_join(thread, NULL);
    printf("Cancelled\n");
    pthread_exit(NULL);

    return 0;
}

//compile g++ main.cpp -pthread
