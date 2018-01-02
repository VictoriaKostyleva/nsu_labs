#include<math.h>
#define size 10
 
void bubbleSort(int* array) // сортировка пузырьком
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
    //cout << "Введите размер массива: ";
 
    //int size;
    int i = 0;
	int array[size];
    //cin >> size;
 
    //int *array = new int[size]; // одномерный динамический массив
    for (i = 0; i < size; i++)
    {
        array[i] = (rand() % 100);
    }
    bubbleSort(array);

    return 0;
}
