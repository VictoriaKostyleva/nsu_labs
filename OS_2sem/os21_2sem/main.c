#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>

#define PHILO 5
#define DELAY 30000
#define FOOD 50

pthread_mutex_t forks[PHILO];
pthread_mutex_t getting_forks_mutex;
pthread_cond_t getting_forks_cond;

pthread_mutex_t foodlock;

pthread_t phils[PHILO];

void *philosopher(void *id);

int food_on_table();

void get_forks(int, int);

void down_forks(int, int);

int sleep_seconds = 0;

int main(int argn, char **argv) {
    int i;

    if (argn == 2)
        sleep_seconds = atoi(argv[1]);

    pthread_mutex_init(&foodlock, NULL);
    for (i = 0; i < PHILO; i++)
        pthread_mutex_init(&forks[i], NULL);
    pthread_mutex_init(&getting_forks_mutex, NULL);
    pthread_cond_init(&getting_forks_cond, NULL);

    for (i = 0; i < PHILO; i++)
        pthread_create(&phils[i], NULL, philosopher, (void *) i);
    for (i = 0; i < PHILO; i++)
        pthread_join(phils[i], NULL);
    return 0;
}

void *philosopher(void *num) {
    int id;
    int i, left_fork, right_fork, f;

    id = (int) num;
    printf("Philosopher %d sitting down to dinner.\n", id);
    right_fork = id;
    left_fork = id + 1;

    /* Wrap around the forks. */
    if (left_fork == PHILO)
        left_fork = 0;

    while (f = food_on_table()) {
        printf("Philosopher %d: get dish %d.\n", id, f);
        get_forks(right_fork, left_fork);

        printf("Philosopher %d: eating.\n", id);
        usleep(DELAY * (FOOD - f + 1));
        down_forks(left_fork, right_fork);
    }
    printf("Philosopher %d is done eating.\n", id);
    return (NULL);
}

int food_on_table() {
    static int food = FOOD;
    int myfood;

    pthread_mutex_lock(&foodlock);
    if (food > 0) {
        food--;
    }
    myfood = food;
    pthread_mutex_unlock(&foodlock);
    return myfood;
}

void get_forks(int fork1, int fork2) {
    int lock;

    pthread_mutex_lock(&getting_forks_mutex);
    do {
        if (!(lock = pthread_mutex_trylock(&forks[fork1]))) {
            if (lock = pthread_mutex_trylock(&forks[fork2])) {
                pthread_mutex_unlock(&forks[fork1]);
            }
        }
        if (lock) {
            pthread_cond_wait(&getting_forks_cond, &getting_forks_mutex);
        }
    } while (lock);
    pthread_mutex_unlock(&getting_forks_mutex);
}

void down_forks(int f1, int f2) {
    pthread_mutex_lock(&getting_forks_mutex);
    pthread_mutex_unlock(&forks[f1]);
    pthread_mutex_unlock(&forks[f2]);
    pthread_cond_broadcast(&getting_forks_cond);
    pthread_mutex_unlock(&getting_forks_mutex);
}