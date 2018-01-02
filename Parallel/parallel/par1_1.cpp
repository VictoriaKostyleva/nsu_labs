#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <iostream>
#include <mpi.h>

typedef unsigned int uint;

const double TAU = 0.01;
const double EPSILON = 0.00001;

const uint TEST = 4;

void matrixMultVector(double* A, double* x, double* result, int width, int height) {
    double sum = 0;
    int i = 0;
    int j = 0;
    for(i = 0; i < height; i++) {
        for(j = 0; j < width; j++) {
            sum += A[i*height + j] * x[j];
        }
        result[i] = sum;
        sum = 0;
    }
    return;
}

void vectorMultScalar(double* vector, double scalar, double* res, int size) {
    int i = 0;
    for(i = 0; i < size; i++) {
        res[i] = scalar * vector[i];
    }
    return;
}

double normal(double* x, int size) {
    double res = 0;
    int i = 0;
    for(i = 0; i < size; i++) {
        res += x[i]*x[i];
    }
    res = sqrt(res);
    return res;
}

void sub(double* a, double* b, double* res, int size) {
    int i = 0;
    for(i = 0; i < size; i++) {
        res[i] = a[i] - b[i];
    }
    return;
}

void printMatrix(double* A,int width, int height, int rank) {
    int i = 0;
    int j = 0;
    for(i = 0; i < height; i++) {
        for(j = 0; j < width; j++) {
            std::cout<<A[i*height + j];
        }
        std::cout<<std::endl;
    }
    std::cout<<rank<<std::endl;
}

bool checkFofFinish(double* A, double* xn, double* b, int size) {
    double* tmp1 = (double*)calloc(1, sizeof(double) * size);
    matrixMultVector(A, xn, tmp1, size, size);
    double* tmp2 = (double*)calloc(1, sizeof(double) * size);
    sub(tmp1, b, tmp2, size);
    double denominator = normal(b, size);
    double numerator = normal(tmp2, size);

    free(tmp1);
    free(tmp2);

    return (numerator / denominator) < EPSILON;
}

double* simpleIteration(double* A, double* x0, double* xn, double* b, const int* sizesOfBlocks, const int* offsets, int size, int rank) {
    xn = x0;
    while(!checkFofFinish(A, xn, b, size)) {
        double* tmp = (double*)calloc(1, sizeof(double) * sizesOfBlocks[rank]);//not size
        matrixMultVector(A, xn, tmp, size, sizesOfBlocks[rank]);
        double* tmpres = (double*)calloc(1, sizeof(double) * size);

       // std::cout << "before!\n";

        MPI_Allgatherv(tmp, sizesOfBlocks[rank], MPI_DOUBLE, tmpres, sizesOfBlocks, offsets, MPI_DOUBLE, MPI_COMM_WORLD);

        //std::cout << "after!\n";

        double* tmp2 = (double*)calloc(1, sizeof(double) * size);
        sub(tmpres, b, tmp2, size);
        double* vectorTau = (double*)calloc(1, sizeof(double) * size);
        vectorMultScalar(tmp2, TAU, vectorTau, size);
        double* res = (double*)calloc(1, sizeof(double) * size);
        sub(xn, vectorTau, res, size);


        //MPI_Barrier(MPI_COMM_WORLD);//if i need it?


        free(tmp);
        free(tmp2);
        free(vectorTau);

        xn = res;
//        free(res);
    }
    return xn;
}

int main(int argc, char* argv[]) {
    int i = 0;
    int j = 0;
    int size;
    int rank;
    MPI_Init(&argc, &argv);//инициализация mpi
    MPI_Comm_size(MPI_COMM_WORLD, &size);//получение числа процессов
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);//получение номера процесса

    printf("rank %d size  %d \n", rank, size);

    uint div = TEST / size;
    uint mod = TEST % size;
    int* sizesOfBlocks = (int*)calloc(1, sizeof(int) * size);//в количестве строк
    int* offsets = (int*)calloc(1, sizeof(int) * size);

    for(i = 0; i < mod; i++) {
        sizesOfBlocks[i] = div + 1;
    }

    for(i = mod; i < size; i++) {
        sizesOfBlocks[i] = div;
    }
    offsets[0] = 0;
    for (i = 1; i < size; i++) {
        offsets[i] = offsets[i - 1] + sizesOfBlocks[i - 1];
    }

    double A[TEST * TEST];
    for (uint i = 0; i < TEST; i++) {
        for (uint j = 0; j < TEST; j++) {
            A[i*TEST + j] = 1.0;
        }
    }
    for (uint i = 0; i < TEST; i++) {
        A[i * TEST + i] = 2.0;
    }

    printMatrix(A, TEST, sizesOfBlocks[rank], rank);

    //double* Ashort = (double*)calloc(1, sizeof(double) * TEST * sizesOfBlocks[rank]);

//    int k = offsets[rank] * TEST;
//    for(i = 0; i < sizesOfBlocks[rank] * TEST; i++) {
//        Ashort[i] = A[k];
//        k++;
//    }
//
//    printMatrix(Ashort, TEST, sizesOfBlocks[rank], rank);

    double* b = (double*)calloc(1, sizeof(double) * TEST);
    for(i = 0; i < TEST; i++) {
        b[i] = TEST + 1;
    }

    double* x0 = (double*)calloc(1, sizeof(double) * TEST);
    double* xn = (double*)calloc(1, sizeof(double) * TEST);

    for(i = 0; i < TEST; i++) {
        for(j = 0; j < TEST; j++) {
            A[i*TEST + j] = 1.;
        }
    }

    for(i = 0; i < TEST; i++) {
        A[i*TEST + i] = 2.;
    }

    xn = simpleIteration(A, x0, xn, b, sizesOfBlocks, offsets, TEST, rank);

    for(i = 0; i < TEST; i++) {
        std::cout << xn[i] << ' ';
    }
    std::cout << std::endl;

    MPI_Finalize();
    return 0;
}
