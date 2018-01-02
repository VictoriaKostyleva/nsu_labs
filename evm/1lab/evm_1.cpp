#define _CRT_SECURE_NO_WARNINGS
 
#include <iostream>
#include <iomanip>//для setw()
#include <ctime>
#include<cstdlib>
#include<stdio.h>//lab
#include<time.h>//lab
#include<sys/times.h>//lab3
#include<unistd.h>
#include<stdint.h>//lab4

using namespace std;
 
void bubbleSort(int* array, int size) // сортировка пузырьком
{
    int temp = 0;
    int i = 0;
    int j = 0;
    for (i = size - 1; i >= 0; i--)
        {
            for (j = 0; j < i; j++)
            {
                if (array[j] > array[j + 1])
                {
                    temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
}
 
int main(int argc, char* argv[])
{
	struct timespec start1, end1;//lab
	

	struct tms start, end;//lab3
	long clocks_per_sec = sysconf(_SC_CLK_TCK);//lab3
	long clocks;//lab3

	/*union ticks
	{
	unsigned long long t64;
	struct s32 {long th, tl;}t32;
	}start2, end2;//lab4*/

	uint32_t hi, lo, hi2, lo2;
	double cpu_Hz = 2500000000;//lab4
	


    srand(time(NULL) ) ;
    setlocale(LC_ALL, "rus");// для использования русского алфавита в консоли
    cout << "Введите размер массива: ";
 
    int size;
    int i = 0;
 
    cin >> size;

	
 
    int *array = new int[size]; // одномерный динамический массив
    for (i = 0; i < size; i++)
    {
        array[i] = (rand() % 100);
        //cout << setw(2) << array[i] << "  "; //setw - отвечает за ширину поля вывода
    }
    //cout << "\n\n";

	//clock_gettime(CLOCK_MONOTONIC_RAW, &start1);//lab
	//times(&start);//lab3
	
	__asm__ __volatile__ ("rdtsc" : "=a" (lo), "=d" (hi));
	uint64_t start2 = ( (uint64_t) lo) | ( ((uint64_t) hi)<< 32 );//lab4

 
    bubbleSort(array, size);

	//clock_gettime(CLOCK_MONOTONIC_RAW, &end1);//lab
	//times(&end);//lab3

	//clocks = end.tms_utime - start.tms_utime;//lab3

	__asm__ __volatile__ ("rdtsc" : "=a" (lo2), "=d" (hi2));
	uint64_t end2 = ( (uint64_t) lo2) | ( ((uint64_t) hi2)<< 32 );//lab4

	

 
    /*for (i = 0; i < size; i++)
    {
        cout << setw(2) << array[i] << "  ";
       
    }
    cout << "\n";*/
 
	
	//printf("Time taken(2): %lf sec.\n", end1.tv_sec-start1.tv_sec + 0.000000001*(end1.tv_nsec-start1.tv_nsec));//lab

	//printf("Time taken(3): %lf sec.\n", (double)clocks / clocks_per_sec);

	printf("(proc_time)Time taken: %lf sec.\n", (end2-start2)/cpu_Hz);//lab4
    //system("pause");
    return 0;
}
