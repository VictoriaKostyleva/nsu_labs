#include <iostream>
#include <vector>

unsigned long long timer(int *arr, int arrSize) {
    union ticks {
        unsigned long long t64;
        struct s32 {
            long th, tl;
        } t32;
    } start, end;

    unsigned  long long tmp = -1;
    for(int j = 0; j < 10; ++j) {
        asm("rdtsc\n":"=a"(start.t32.th), "=d"(start.t32.tl));
        for (int i = 0, index = 0, count = arrSize; i < count; ++i) {
            index = arr[index];
        }
        asm("rdtsc\n":"=a"(end.t32.th), "=d"(end.t32.tl));

        if(tmp > (end.t64 - start.t64)) {
            tmp = end.t64 - start.t64;
        }
    }

    return tmp / arrSize;
}

int main() {
    const int cacheSize = 3072 * 1024; //KB 
    std::vector<unsigned long long>results;

    for(int fragmentCount = 1; fragmentCount <= 32; ++fragmentCount) {
        const int elemCount = cacheSize / sizeof(int) / fragmentCount;
        const int arrSize = cacheSize / sizeof(int) * fragmentCount;
        int *arr = new int[arrSize];

        for(int i = 0; i < fragmentCount; ++i) {
            for(int j = 0; j < elemCount; ++j) {
                arr[i * cacheSize / sizeof(int) + j] = (i + 1) * cacheSize / sizeof(int) + j;
            }
        }
        for(int i = 0; i < elemCount; ++i) {
            arr[(fragmentCount - 1) * cacheSize / sizeof(int) + i] = i + 1;
        }
        arr[(fragmentCount - 1) * (cacheSize / sizeof(int) - 1)] = 0;

        results.push_back(timer(arr, arrSize));//push_back использкется для добавления нового элемента в конец вектора

        delete[] arr;
    }


    for(const auto &v : results) {
        std::cout << v << std::endl;
    }
    return 0;
}
