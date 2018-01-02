#include<stdio.h>
#include<stdlib.h>
#include<mpi/mpi.h>

#define NUM_DIMS 3
/* NUM_DIMS - размер декартовой топологии. "двумерная решетка" */

//#define M 9
#define N 9
//#define K 9

#define A(i, j) A[N*i+j]
#define B(i, j) B[k*i+j]
#define C(i, j) C[k*i+j]
#define AA(i, j) AA[N *i+j]   //
#define BB(i, j) BB[k*i+j]
#define CC(i, j) CC[k*i+j]

int main(int argc, char **argv) {
    MPI_Init(&argc, &argv);

    int threadCount;//количество узлов решетки
    int threadRank;
    MPI_Comm_size(MPI_COMM_WORLD, &threadCount);

    int dims[NUM_DIMS - 1] = {NUM_DIMS, NUM_DIMS};
    //целочисленный массив размера ndims, хранящий количество процес-сов по каждой координате

    //Создаем решетку
    int periods[2] = {0};//как в презентахе

    MPI_Comm comm_2D;
    MPI_Comm comm_1D[2];

    MPI_Cart_create(MPI_COMM_WORLD, 2, dims, periods, 0, &comm_2D);//why zero?
    MPI_Comm_rank(comm_2D, &threadRank);

    int k = N/dims[1];

    int *A = (int*)calloc(N*N, sizeof(int));
    int *B = (int*)calloc(N*N, sizeof(int));
    int *C = (int*)calloc(N*N, sizeof(int));

    double startTime = MPI_Wtime();

    int subdims[2];
    subdims[0] = 0;
    subdims[1] = 1;
    MPI_Cart_sub(comm_2D, subdims, &comm_1D[0]);
    subdims[0] = 1;
    subdims[1] = 0;
    MPI_Cart_sub(comm_2D, subdims, &comm_1D[1]);

    MPI_Datatype column, matrix;
    MPI_Type_vector(N, N / k, N, MPI_INT, &column);
    MPI_Type_create_resized(column, 0, N / k * sizeof(int), &column);
    MPI_Type_commit(&column);

    int *AA, *BB, *CC;
    AA = (int*)calloc(N * k, sizeof(int));
    BB = (int*)calloc(N * k, sizeof(int));
    CC = (int*)calloc(k * k , sizeof(int));

    int threadCoords[2];
    MPI_Comm_rank(comm_2D, &threadRank);
    MPI_Cart_coords(comm_2D, threadRank, NUM_DIMS - 1, threadCoords);

    if (threadCoords[0] == 0) {
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {
                A(i, j) = 1;
                B(i, j) = 1;
            }
        }
    }

    if (threadCoords[1] == 0) {
        MPI_Scatter(A, N * k, MPI_INT, AA, N * k, MPI_INT, 0, comm_1D[0]);
    }

    if (threadCoords[0] == 0) {
        int offset[3] = {0, 1, 2};
        int send[3] = {1, 1, 1};
        MPI_Scatterv(B, send, offset, column, BB, N * k , MPI_INT, 0, comm_1D[1]);
    }

    MPI_Bcast(AA, k*N, MPI_INT, 0, comm_1D[1]);
    MPI_Bcast(BB, k*N, MPI_INT, 0, comm_1D[0]);

    int i, j, t;
    for(i = 0; i < k; i++){
        for(j = 0; j < k; j++){
            for(t = 0; t < N; t++){
                CC[i*k+j] += AA[i*N+t] * BB[t*k+j];
            }
        }
    }

    MPI_Type_vector(N / k, N / k, N, MPI_INT, &matrix);
    MPI_Type_create_resized(matrix, 0, N / k * sizeof(int), &matrix);
    MPI_Type_commit(&matrix);

    for (int i = 0; i < k; i++) {
        for (int j = 0; j < k; j++) {
            for (int t = 0; t < NUM_DIMS; t++) {
                CC(i, j) = CC(i, j) + AA(i, t) * BB(t, j);
            }
        }
    }

    t = 0;
    int countc[dims[0]*dims[1]], dispc[dims[0]*dims[1]];
    for( i = 0 ; i < dims[0] ; i++){
        for(j = 0 ; j < dims[0] ; j++){
            countc[t] = 1;
            dispc[t] = (i*N + j);
            t++;
        }
    }

        //Собираем результат
    MPI_Gatherv(CC, k * k, MPI_INT, C, countc, dispc, matrix, 0, comm_2D);


        free(AA);
        free(BB);
        free(CC);

        MPI_Comm_free(&comm_2D);
        for (int i = 0; i < 2; i++) {
            MPI_Comm_free(&comm_1D[i]);
        }
        if (threadRank == 0) {

            MPI_Type_free(&column);
            MPI_Type_free(&matrix);
        }



    double finishTime = MPI_Wtime();

    bool flag = true;

    for (int i = 0; i < N; ++i) {
        for (int j = 0; j < k; ++j) {
            if(C(i, j) != N) {
                flag =  false;
                break;
            }
        }

        flag =  true;
    }

    if(threadRank == 0) {
        printf("Time: %lf\nCheck the result: %s\n", finishTime - startTime, flag ? "good" : "bad");

    }

    MPI_Finalize();
    return 0;
}
