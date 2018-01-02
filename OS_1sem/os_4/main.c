#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct list
{
    char* data;
    struct list *next;
}List;

List* makelist()
{
    List* head = NULL;
    List* p = NULL;
    List* t = NULL;
    char str[256];
    head = (List*)malloc(sizeof(List));
    gets(str);
    if(str[0] == '.')
    {
        return head;
    }

    head->data = (char*)malloc(sizeof(char) * (strlen(str) + 1));
    //head->data = str;//плохо?
    memcpy(head->data, str, strlen(str) + 1);
    head->next = NULL;

    t = (List*)malloc(sizeof(List));
    t->data = (char*)malloc(sizeof(char) * (strlen(str) + 1));
    t = head;

    while(gets(str))
    {
        if(str[0] == '.')
        {
            return head;
        }
        p = (List*)malloc(sizeof(List));
        p->data = (char*)malloc(sizeof(char) * (strlen(str) + 1));
        //p->data = str;//?
        memcpy(p->data, str, strlen(str) + 1);
        p->next = NULL;
        t->next = p;
        t = p;
    }
    return head;
}

int main() {
    printf("Enter a string:\n");

    List *list = NULL;
    List *p = NULL;

    list = makelist();

    while(list != NULL)
    {
        p = list;
        list = list->next;
        printf("%s\n", p->data);
        free(p->data);
        free(p);
    }
    return 0;
}
