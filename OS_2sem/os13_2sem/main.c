#include <pthread.h>
#include <stdio.h>
#include <semaphore.h>
#include <string.h>

#define ITERATIONS 10

sem_t p;
sem_t c;

void* print_message(void* args) {
    int i = 0;
    char* text = (char*)args;
    for (i = 0; i < ITERATIONS; i++) {
	     sem_wait(&p);
	     printf("%s\n", text);
	     sem_post(&c);
    }
    return NULL;
}

int main (int argc, char* argv) {
    pthread_t thread;
    int i = 0;
    char* child = "child";
    char* parent = "parent";
  int status;

    sem_init (&p, 0, 1);
    sem_init (&c, 0, 0);

  status = pthread_create(&thread, NULL, print_message, (void*)child);

  if (status != 0) {
      char buf[256];
      strerror_r(status, buf, sizeof buf);
      printf("%s\n", buf);
      exit(1);
  }

    for (i = 0; i < ITERATIONS; i++) {
	     sem_wait(&c);
	     printf ("%s\n", parent);
	     sem_post(&p);
    }


    pthread_join(thread, NULL);

    sem_destroy(&p);
    sem_destroy(&c);
    pthread_exit (NULL);
}
