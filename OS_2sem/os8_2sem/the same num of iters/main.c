#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <signal.h>

typedef struct {
    double pi;
    int index;
} Pi_struct;

#define STEPS 200000
int threads_num = 4;
int maxiter = 0;

int stopped_flag = 0;
pthread_mutex_t maxiterlock = PTHREAD_MUTEX_INITIALIZER;

void *pi_func(void *param) {
    double pi_count = 0.0;

    int i = 0;
    int j = 0;
    for (i = ((Pi_struct *) param)->index;; i += threads_num) {
        pi_count += 1.0 / (i * 4.0 + 1.0);
        pi_count -= 1.0 / (i * 4.0 + 3.0);
        j += 1;
        if (j % STEPS == 0 && stopped_flag) {
            pthread_mutex_lock(&maxiterlock);
            if (maxiter < j) {
                maxiter = j;
            }
          //  printf("Thread %d is working, %d iterations\n", ((Pi_struct *) param)->index, j);
            pthread_mutex_unlock(&maxiterlock);

                if (maxiter <= j) {
                    printf("Thread %d, %d iterations\n", ((Pi_struct *) param)->index, j);
                    ((Pi_struct *) param)->pi = pi_count;
                    pthread_exit((void *) param);
            }
        }
    }
}

void sighandler(int signum) {
    stopped_flag = 1;
}

int main(int argc, char **argv) {

    double pi = 0;
    int i;
    pthread_t *ids;
    Pi_struct *params;
    int status;
    int *maxiter;

    signal(SIGINT, sighandler);

    if (argc == 2) threads_num = atoi(argv[1]);

    params = (Pi_struct *) malloc(threads_num * sizeof(Pi_struct));
    ids = (pthread_t *) malloc(threads_num * sizeof(pthread_t));
    maxiter = (int *) malloc(sizeof(int));

    for (i = 0; i < threads_num; i++) {
        params[i].index = i;
        status = pthread_create(ids + i, NULL, pi_func, (void *) (params + i));
        if (status != 0) {
            char buf[256];
            strerror_r(status, buf, sizeof buf);
            printf("%s\n", buf);
            exit(1);
        }
    }

    for (i = 0; i < threads_num; i++) {
        Pi_struct *res;
        pthread_join(ids[i], (void **) &res);
        pi += res->pi;
        //pi+= params[i].pi;
    }

    pi *= 4.0;

    free(params);
    free(ids);

    printf("pi: %.16f\n", pi);

    pthread_mutex_destroy(&maxiterlock);
    return 0;
}

//compile gcc main.c -pthread
