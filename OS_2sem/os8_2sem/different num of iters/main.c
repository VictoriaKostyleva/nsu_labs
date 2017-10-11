#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <signal.h>

typedef struct {
    double pi;
    long long index;
} Pi_struct;

#define STEPS 200000
int threads_num = 4;
int stopped_flag = 0;

void *pi_func(void *param) {
    double pi_count = 0.0;
    int cycles = 0;
    int iters = 0;

    long long i = ((Pi_struct *) param)->index;
    for (i;; i += threads_num) {
        iters++;
        pi_count += 1.0 / (i * 4.0 + 1.0);
        pi_count -= 1.0 / (i * 4.0 + 3.0);

        if (STEPS == iters) {
            iters = 0;
            ++cycles;
            if (1 == stopped_flag) {
                break;
            }
        }
        ((Pi_struct *) param)->pi = pi_count;
    }
    printf("Thread: %lld, sum: %.16f, cyc: %d\n", ((Pi_struct *) param)->index, pi_count, cycles);

    pthread_exit((void *) param);
    //return param;
}


void sighandler(int signum) {
    if (stopped_flag == 0) {
        stopped_flag = 1;
    } else
        exit(0);
}

int main(int argc, char **argv) {

    double pi = 0;
    int i;
    pthread_t *ids;
    Pi_struct *params;
    int status;
    pthread_barrier_t barrier;


    pthread_barrier_init(&barrier, NULL, threads_num);

    signal(SIGINT, sighandler);

    if (argc == 2) threads_num = atoi(argv[1]);

    params = (Pi_struct *) malloc(threads_num * sizeof(Pi_struct));
    ids = (pthread_t *) malloc(threads_num * sizeof(pthread_t));

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
        //free(res);
    }

    pi *= 4.0;


    pthread_barrier_destroy(&barrier);

    free(params);
    free(ids);

    printf("pi: %.16f, iters: %d\n", pi, STEPS);

    return 0;
}

//compile gcc main.c -pthread
