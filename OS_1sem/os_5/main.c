#include <stdio.h>
#include <fcntl.h>//open
#include <unistd.h>//close

#define ERROR -1
#define NUM_LINES 100

struct file
{
    int offset;
    int size;
};

int countFile(int handle, struct file *lines, int *countLines)
{
    char c = 0;
    ssize_t code = 0;
    //read - число байт, помощенных в буфер/0 при EOF/-1 при ошибке

    while((code = read(handle, &c, sizeof(char))) > 0)
    {
        if (c != '\n')
        {
            lines[*countLines].size++;
            continue;
        }

        (*countLines)++;

        if (*countLines > NUM_LINES)
        {
            printf(stderr, "File is too long\n");
            return ERROR;
        }
        lines[*countLines].offset = lines[*countLines - 1].offset + lines[*countLines - 1].size + 1;
    }
    return 0;
}

int printLines(int handle, struct file *lines)
{
    int num = -1;
    char c = 0;
    printf("Enter a number of a string:\n");
    while (scanf("%d", &num))
    {
        if(0 == num)//num - the number of the line
        {
            printf("Finish of working\n");
            return 0;
        }
        if(num < 0 || num > NUM_LINES)
        {
            printf("stderr, Wrong argument\n");
            continue;
        }

        if(lseek(handle, lines[num - 1].offset, SEEK_SET) == ERROR)
        {
            printf(stderr, "Error in opening the file\n");
            return ERROR;
        }
//         SEEK_SET - from the beginning of the file
//         SEEK_CUR - from the current position
//         SEEK_END - from the end

        for(int i = 0; i < lines[num - 1].size; i++)
        {
            if(read(handle, &c, sizeof(char)) <= 0)
            {
                printf(stderr, "Error in reading the file\n");
                return ERROR;
            }
            printf("%c", c);
        }
        printf("\n");
    }

    printf(stderr, "Scanf error\n");
    return ERROR;

}

int main(int argc, char** argv) {
    int countLines = 0;

    if(argc < 2)
    {
        printf(stderr, "Need a name of a file\n");
        return 1;
    }

    int input = open(argv[1], O_RDONLY);//returns handle

    if(input == ERROR)
    {
        printf(stderr, "Error in opening the file\n");
        return ERROR;
    }

    struct file lines[NUM_LINES] = {};//

    if(countFile(input, lines, &countLines) != 0)
    {
        close(input);
        return 1;
    }

    if(countLines == 0)
    {
        printf(stderr, "The file is empty\n");
        close(input);
        return 0;
    }

    if(printLines(input, lines) != 0)
    {
        close(input);
        return 1;
    }

    close(input);
    return 0;
}