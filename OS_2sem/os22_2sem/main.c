#include <stdio.h>
#include <unistd.h>
#include <semaphore.h>
#include <pthread.h>
#include <signal.h>

sem_t semA, semB, semC, semAB;

volatile int flag = 1;

void sighandler(int sig) {
    flag = 0;
    printf("\n");
    sem_post(&semA);
    sem_post(&semB);
    sem_post(&semC);
}

void *createA(void *argv) {
    int i = 0;
    while (flag) {
        sleep(1);
        if(!flag) {
          printf("Fake A#%d\n", i++);
          break;
        }

        if (0 != sem_post(&semA)) {
            printf("Err\n");
            return NULL;
        }
        printf("Made A#%d\n", i++);
    }
    pthread_exit(NULL);
}

void *createB(void *argv) {
    int i = 0;
    while (flag) {
        sleep(2);
        if(!flag) {
          printf("Fake B#%d\n", i++);
          break;
        }
        if (0 != sem_post(&semB)) {
            printf("Err\n");
            return NULL;
        }
        printf("Made B#%d\n", i++);
    }
    pthread_exit(NULL);
}

void *createC(void *argv) {
    int i = 0;
    while (flag) {
        sleep(3);
        if(!flag) {
          printf("Fake C#%d\n", i++);
          break;
        }
        if (0 != sem_post(&semC)) {
            printf("Err\n");
            return NULL;
        }
        printf("Made C#%d\n", i++);
    }
    pthread_exit(NULL);
}

void *createAB(void *argv) {
    int i = 0;
    while (flag) {
        if (0 != sem_wait(&semA)) {
            printf("Err\n");
            return NULL;
        }
        if (0 != sem_wait(&semB)) {
            printf("Err\n");
            return NULL;
        }

        if(!flag) {
          if (0 != sem_post(&semAB)) {
              printf("Err\n");
              return NULL;
          }
          printf("Fake AB#%d\n", i++);
          break;
        }
        if (0 != sem_post(&semAB)) {
            printf("Err\n");
            return NULL;
        }
        printf("Made AB#%d\n", i++);
    }
    pthread_exit(NULL);
}

void *createW() {
    int i = 0;
    while (flag) {
        if (0 != sem_wait(&semAB)) {
            printf("Err\n");
            return NULL;
        }
        if(!flag) {
          printf("Fake W#%d\n", i++);
          break;
        }
        if (0 != sem_wait(&semC)) {
            printf("Err\n");
            return NULL;
        }
        printf("Made W#%d\n", i++);
    }
}

int main() {
    pthread_t threadA;
    pthread_t threadB;
    pthread_t threadC;
    pthread_t threadAB;

    signal(SIGINT, sighandler);

    if (0 != sem_init(&semA, 0, 0)) {
        printf("Err\n");
        return -1;
    }
    if (0 != sem_init(&semB, 0, 0)) {
        printf("Err\n");
        return -1;
    }
    if (0 != sem_init(&semC, 0, 0)) {
        printf("Err\n");
        return -1;
    }
    if (0 != sem_init(&semAB, 0, 0)) {
        printf("Err\n");
        return -1;
    }
    if (0 != pthread_create(&threadA, NULL, createA, NULL)) {
        printf("Err\n");
        return -1;
    }
    if (0 != pthread_create(&threadB, NULL, createB, NULL)) {
        printf("Err\n");
        return -1;
    }
    if (0 != pthread_create(&threadC, NULL, createC, NULL)) {
        printf("Err\n");
        return -1;
    }
    if (0 != pthread_create(&threadAB, NULL, createAB, NULL)) {
        printf("Err\n");
        return -1;
    }
    createW();
    if (0 != sem_destroy(&semA)) {
        printf("Err\n");
    }
    if (0 != sem_destroy(&semB)) {
        printf("Err\n");
    }
    if (0 != sem_destroy(&semC)) {
        printf("Err\n");
    }
    if (0 != sem_destroy(&semAB)) {
        printf("Err\n");
    }

    pthread_exit(NULL);
    return 0;
}
