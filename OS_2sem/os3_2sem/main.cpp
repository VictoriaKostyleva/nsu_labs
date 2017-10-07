#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>
void* func(void *args) {
    //int i = 0;
    char** text;
    for(text=(char**)args; *text!=NULL; text++) {

      printf("%s\n", *text);
    }
    return NULL;
}

int main() {
  pthread_t thread1;
	pthread_t thread2;
	pthread_t thread3;
	pthread_t thread4;

    static char *one[] = {"one", "one1", "one2", NULL};
    static char *two[] = {"two", "two1", "two2", NULL};
    static char *three[] = {"three", "three1", "three2", NULL};
    static char *four[] = {"four", "four1", "four2", NULL};

    int status = pthread_create(&thread1, NULL, func, one);
    if (status != 0) {
        char buf[256];
        strerror_r(status, buf, sizeof buf);
        printf("%s\n", buf);
        exit(1);
    }
    int status2 = pthread_create(&thread2, NULL, func, two);
    if (status2 != 0) {
        char buf[256];
        strerror_r(status2, buf, sizeof buf);
        printf("%s\n", buf);
        exit(1);
    }
    int status3 = pthread_create(&thread3, NULL, func, three);
    if (status3 != 0) {
        char buf[256];
        strerror_r(status3, buf, sizeof buf);
        printf("%s\n", buf);
        exit(1);
    }
    int status4 = pthread_create(&thread4, NULL, func, four);
    if (status4 != 0) {
        char buf[256];
        strerror_r(status4, buf, sizeof buf);
        printf("%s\n", buf);
        exit(1);
    }

    pthread_exit(NULL);

    return 0;
}

//compile g++ main.cpp -pthread
