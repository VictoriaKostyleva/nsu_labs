#include <iostream>
#include <ctime>
#include <sstream>
#include <cstdlib>
#include <algorithm>

void serial(int *array, int N)//последовательный обход
{
    for (int j = 0; j < N; ++j)
    {
        array[j] = j + 1;
    }
    array[N - 1] = 0;
}

void reverse(int *array, int N) //обратный
{
    for (int j = N - 1; j >= 0; --j)
    {
        array[j] = j - 1;
    }
    array[0] = N - 1;
}

void random(int *array, int N)
{
    for (int i = 0; i < N; ++i)
    {
        array[i] = i;
    }

    for (int i = N - 1; i > 0; --i)
    {
        int j = rand() % i;
        std::swap(array[i], array[j]);
    }
}

unsigned long long timer(int *arr, int N, int K)
{
    union ticks {
        unsigned long long t64;
        struct s32 {
            long th, tl;
        } t32;
    } start, end;

    //Разогрев кэша
    for (int j = 0, index = 0; j < N; ++j)
    {
        index = arr[index];
    }

    unsigned long long result = -1;
    for(int q = 0; q < 5; ++q) {
        asm("rdtsc\n":"=a"(start.t32.th), "=d"(start.t32.tl));
        //Обход
        for (int j = 0, index = 0, count = N * K; j < count; ++j) {
            index = arr[index];
        }

        asm("rdtsc\n":"=a"(end.t32.th), "=d"(end.t32.tl));
        unsigned long long tmpResult = (end.t64 - start.t64) / (N * K);
        if(tmpResult < result) {
            result = tmpResult;
        }
    }
    return result;
}

int main() {

    int N = 256; //1Kb
    const int K = 10;
    std::stringstream serialStream;
    std::stringstream reverseStream;
    std::stringstream randomStream;
    //до 8192 Kb
    for(int i = 0; N <= 8192 * 256; ++i, N += N < (512 * 256) ? N : (512 * 256))
    {
        int *arr = new int[N];
        unsigned long long result;

        //последовательный обход
        serial(arr, N);
        serialStream << (N / 256) << "KB: " << timer(arr, N, K) << std::endl;

        //обратный обход
        reverse(arr, N);
        reverseStream << (N / 256) << "KB: " << timer(arr, N, K) << std::endl;

        //рандомный обход
        random(arr, N);
        randomStream << (N / 256) << "KB: " << timer(arr, N, K) << std::endl;

        delete[] arr;
    }
    std::cout << "Serial\n" << serialStream.str() << std::endl;
    std::cout << "Reverse\n" << reverseStream.str() << std::endl;
    std::cout << "Random\n" << randomStream.str() << std::endl;

    return 0;
}

