#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>

#define ITERATIONS 10
#define BUF_SIZE 256

pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t cond = PTHREAD_COND_INITIALIZER;

void *func(void *args) {
    int i = 0;
    char *text = (char *)args;

    pthread_mutex_lock(&mutex);
    for (i = 0; i < ITERATIONS; i++) {
        printf("%s\n", text);
        pthread_cond_signal(&cond);
        pthread_cond_wait(&cond, &mutex);
    }
    pthread_mutex_unlock(&mutex);
    pthread_cond_signal(&cond);
    return NULL;
}

int main() {
    pthread_t thread;
    int status;

    char *child = "child";
    char *parent = "parent";

    status = pthread_create(&thread, NULL, func, (void *)child);

    if (status != 0) {
        char buf[BUF_SIZE];
        strerror_r(status, buf, sizeof buf);
        printf("%s\n", buf);
        exit(1);
    } else {
        func(parent);
    }

    pthread_mutex_destroy(&mutex);
    pthread_exit(NULL);

    return 0;
}

//compile g++ main.cpp -pthread
