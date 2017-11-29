#include <pthread.h>
#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <stdlib.h>

#define ITERATIONS 10
#define MUTEX_COUNT 3

pthread_mutex_t mutexes[MUTEX_COUNT];

int last_mutex(int k) {
	if(k == 0) {
		return 2;
	}
		else
		return k - 1;
}

void *print_text(void *args) {
    char *text = (char *) args;
    int k;
    int i = 0;

    if (!strcmp(text, "child")) {
        pthread_mutex_lock(&mutexes[1]);
        k = 2;
    } else {
        pthread_mutex_lock(&mutexes[2]);
        while (!pthread_mutex_trylock(&(mutexes[1]))) {
            pthread_mutex_unlock(&(mutexes[1]));
        }
        k = 0;
    }

    for (i = 0; i < ITERATIONS; i++) {
        pthread_mutex_lock(&mutexes[k]);
        printf("%s\n", text);
        pthread_mutex_unlock(&mutexes[last_mutex(k)]);

        k = (k + 1) % MUTEX_COUNT;
    }

    if (!strcmp(text, "child")) {
        pthread_mutex_unlock(&mutexes[last_mutex(k)]);
        pthread_mutex_unlock(&mutexes[k]);
    } else {
        pthread_mutex_unlock(&mutexes[last_mutex(k)]);
    }


}

int main(int argc, char const *argv[]) {
    pthread_t pthread;
    int i = 0;
    int status;
    for (i = 0; i < MUTEX_COUNT; i++) {
        pthread_mutex_init(&mutexes[i], NULL);
    }

    char *child = "child";
    char *parent = "parent";

    status = pthread_create(&pthread, NULL, print_text, (void *) child);
    if (status != 0) {
        char buf[256];
        strerror_r(status, buf, sizeof buf);
        printf("%s\n", buf);
        exit(1);
    }

    print_text(parent);

    pthread_join(pthread, NULL);
    for (i = 0; i < MUTEX_COUNT; i++)
        pthread_mutex_destroy(&mutexes[i]);

    pthread_exit(NULL);
}
