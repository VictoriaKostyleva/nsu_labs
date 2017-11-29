#include <fcntl.h>
#include <semaphore.h>
#include <stdio.h>
#include <unistd.h>

#define ITERATIONS 10

int main() {
    sem_t *p;
    sem_t *c;
    pid_t child_pid;

    p = sem_open("/parent", O_CREAT | O_EXCL, O_RDWR, 0);

    if (p == SEM_FAILED) {
        printf("open error!\n");
        return -1;
    }

    c = sem_open("/child", O_CREAT | O_EXCL, O_RDWR, 1);

    if (c == SEM_FAILED) {
        printf("open error!\n");
        return -1;
    }

    if (child_pid = fork()) {
        for (int i = 0; i < ITERATIONS; i++) {
            if (0 != sem_wait(c)) {
                printf("wait error!\n");
                return -1;
            }
            printf("child\n");
            if (0 != sem_post(p)) {
                printf("wait error!\n");
                return -1;
            }
        }
    } else {
        if (child_pid < 0) {
            printf("fork error!\n");
            return -1;
        }

        for (int i = 0; i < ITERATIONS; i++) {
            if (0 != sem_wait(p)) {
                printf("wait error!\n");
                return -1;
            }
            printf("parent\n");
            if (0 != sem_post(c)) {
                printf("post error!\n");
                return -1;
            }
        }

				if (0 != sem_unlink("/child")) {
            printf("unlink error!\n");
            return -1;
        }
        if (0 != sem_unlink("/parent")) {
            printf("unlink error!\n");
            return -1;
        }

        if (0 != sem_close(c)) {
            printf("close error!\n");
            return -1;
        }
        if (0 != sem_close(p)) {
            printf("close error!\n");
            return -1;
        }
    }
    return 0;
}
