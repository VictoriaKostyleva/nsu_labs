#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

typedef struct {
  double pi;
	long long index;
} Pi_struct;

#define STEPS 200000000
int threads_num = 4;

void* pi_func(void* param) {
		double pi_count = 0.0;
		long long i = ((Pi_struct *)param)->index;
		for (i; i < STEPS ; i+=threads_num) {
				pi_count += 1.0 / (i * 4.0 + 1.0);
				pi_count -= 1.0 / (i * 4.0 + 3.0);
		}
    ((Pi_struct *)param)->pi = pi_count;

    printf("Thread: %lld, sum: %.16f\n", ((Pi_struct *)param)->index, pi_count);
		return param;
}

int main(int argc, char** argv) {

  	double pi = 0;
  	int i;
	pthread_t* ids;
	Pi_struct* params;
	int status;

	if(argc == 2) threads_num = atoi(argv[1]);

	params = (Pi_struct*)malloc(threads_num * sizeof(Pi_struct));
	ids = (pthread_t*)malloc(threads_num * sizeof(pthread_t));

    for(i = 0; i < threads_num; i++) {
			params[i].index = i;
			status = pthread_create(ids+i, NULL, pi_func, (void*)(params+i));
      if (status != 0) {
          char buf[256];
          strerror_r(status, buf, sizeof buf);
          printf("%s\n", buf);
          exit(1);
      }
	}

	for(i = 0; i < threads_num; i++) {
    Pi_struct* res;
    pthread_join(ids[i], (void **)&res);
    pi += res->pi;
    //pi+= params[i].pi;
	}

	pi *= 4.0;

  free(params);
  free(ids);

	printf ("pi: %.16f, iters: %d\n", pi, STEPS);

  return 0;
}

//compile gcc main.cpp -pthread
