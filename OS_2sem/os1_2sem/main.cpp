#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>

#define ITERATIONS 10

void* func(void *args) {
    int i = 0;
    char* text = (char*)args;
    for(i = 0; i < ITERATIONS; i++) {
    printf("%s\n", text);
}
    return NULL;
}

int main() {
    pthread_t thread;
    int status;

    char* child = "child";
    char* parent = "parent";

    status = pthread_create(&thread, NULL, func, (void*)child);

    if (status != 0) {
        char buf[256];
        strerror_r(status, buf, sizeof buf);
        printf("%s\n", buf);
        exit(1);
    }

    func(parent);
    pthread_exit(NULL);

    return 0;
}

//compile g++ main.cpp -pthread
