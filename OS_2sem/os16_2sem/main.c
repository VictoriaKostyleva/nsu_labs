#include <stdio.h>
#include <assert.h>
#include <malloc.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>
#include <stdlib.h>
#include <signal.h>

#define MAX_LEN (80)
#define TIMEOUT 5
pthread_mutex_t global_mutex;
int flag = 0;

typedef struct list_elem {
    struct list_elem *next;
    char *data;
} list_elem;

typedef struct head_list_elem {
    struct list_elem *first;
    int size;
} head_list_elem;

void sighandler(int signum) {
  flag = 1;
  printf("%s\n", "got it");
  signal(SIGINT, sighandler);
}

void add_element(head_list_elem *head, char *str) {
    list_elem *tmp = NULL;
    list_elem *new_element = NULL;

    tmp = head->first;
    pthread_mutex_lock(&global_mutex);

    new_element = (list_elem *) malloc(sizeof(list_elem));
    if (new_element == NULL) {
        printf("%s\n", "Error in malloc");
        exit(1);
    }

    (head->size)++;

    if (NULL == tmp) {//если список пуст
        new_element->next = NULL;
    } else {
        new_element->next = (head->first);
    }
    new_element->data = str;
    head->first = new_element;

    pthread_mutex_unlock(&global_mutex);
    return;
}

void free_list(head_list_elem *head) {
    list_elem *cur = head->first;
    list_elem *tmp = NULL;
    free(head);
    while (cur) {
        tmp = cur->next;
        free(cur);
        cur = tmp;
    }
}

void swap(list_elem *a, list_elem *b) {
    void *tmp = a->data;
    a->data = b->data;
    b->data = tmp;
}

void print_list(head_list_elem *head) {
    pthread_mutex_lock(&global_mutex);
    list_elem *cur = head->first;
    printf("-----------------List-----------------\n");
    while (cur) {
        printf("%s\n", cur->data);
        cur = cur->next;
    }
    printf("-----------------End-----------------\n");
    pthread_mutex_unlock(&global_mutex);
}

void *sort_list(void *head) {
    if (head == NULL) {
        return;
    }

    while (1) {
        list_elem *list = ((head_list_elem *) head)->first;
        list_elem *iteri = NULL;
        sleep(TIMEOUT);

        pthread_mutex_lock(&global_mutex);
        for (iteri = list; iteri; iteri = iteri->next) {
            list_elem *iterj = NULL;
            for (iterj = iteri->next; iterj; iterj = iterj->next) {
                if (0 < strcmp(iteri->data, iterj->data)) {

                    swap(iteri, iterj);
                }
            }
        }
        pthread_mutex_unlock(&global_mutex);
        printf("%s\n", "Sorted");

    }
    return NULL;

}



int main(int argc, char *argv[]) {
    char *cur = NULL;
    pthread_t sort_thread;

    pthread_mutex_init(&global_mutex, NULL);
    signal(SIGINT, sighandler);

    head_list_elem *head = NULL;
    head = (head_list_elem *) malloc(sizeof(head_list_elem));
    if (head == NULL) {
        printf("%s\n", "Error in malloc");
        exit(1);
    }
    head->size = 0;
    head->first = NULL;

    pthread_create(&sort_thread, NULL, sort_list, (void *) (head));

    while (1) {

        cur = (char *) calloc(MAX_LEN, sizeof(char));
        if (cur == NULL) {
            printf("%s\n", "Error in calloc");
            exit(1);
        }

        fgets(cur, MAX_LEN, stdin);

        if ('\n' == cur[strlen(cur) - 1]) {//если \n, то \n в строчку класть не будем
            cur[strlen(cur) - 1] = 0;
        }

        if (0 == strlen(cur)) {
            print_list(head);
            continue;
        }
        add_element(head, cur);
    }

    printf("%s\n", "boo");
    pthread_cancel(sort_thread);
    pthread_join(sort_thread, NULL);
    pthread_mutex_destroy(&global_mutex);
    free_list(head);

    return 0;

}
