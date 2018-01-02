#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>

void matr_mk(int N, double* a)//заполняем диагональ матрицы
{
	int i,j;
	for (i = 0; i < N; i++)
    {
		a[i*N+i] = i+1;
	}
}

void print(int N, char * name, double* array)
{
	int i, j;
	printf("\n%s\n",name);
	for (i = 0 ; i < N ; i++)
    {
		for (j = 0 ; j < N ; j++)
        {
			printf("%0.5f  ", array[N*i + j]);
		}
		printf("\n");
	}
}

void transpose(double* a, double *t, int N)//транспонирование матрицы
{
	for (int i = 0; i < N; i++)
    {
		for(int j = 0; j < N; j++)
        {
			t[i*N + j] = a[j*N + i];
		}
	}
}

double get_aij(double *matr, int row, int column, int N)
{
	return matr[row * N + column];
}

double maxj(double *matr, int N)//максимальный столбец
{
  double max = 0;
  for(int i = 0; i < N; i++)
  {
    	double tmp = 0;
      for(int j = 0; j < N; j++)
      {
          tmp += abs(get_aij(matr, j, i, N));
      }
      if(max < tmp)
      {
          max = tmp;
      }
  }
  return max;
}

double maxi(double *matr, int N) //максимальная строка
{
	double max = 0;
  for(int i = 0; i < N; i++)
  {
      double tmp = 0;
      for(int j = 0; j < N; j++)
      {
          tmp += abs(get_aij(matr, i, j, N));
      }
      if(max < tmp)
      {
          max = tmp;
      }
  }

 return max;
}

void divsc(double *matr, double *tr, int N, double r)//поиск В, собственно деление
{
	for(int i = 0; i < N; i++)
    {
		for(int j = 0 ; j < N; j++)
        {
			matr[i*N+j] = tr[i*N+j] / r;
		}
	}
}

void mult(double *m1, double *m2, double *m3, int N)//перемножение двух матриц
{
	double *y=(double*)malloc(sizeof(double)*N*N);
	transpose(m2, y, N);

	for (int row = 0; row < N; row++)
    {
    for (int col = 0; col < N; col++)
    {
			m3[row * N + col] = 0;
      for (int inner = 0; inner < N; inner++)
      {
          m3[row * N + col] += m1[row * N + inner] * y[col * N + inner];
      }
    }
  }

	free(y);
}

void make_r(double *a, double *b, double *I, double *r, int N)//R
{
	double *y=(double*)malloc(sizeof(double)*N*N);
	mult(b, a, y, N);
	for(int i = 0; i < N; i++)
    {
		for(int j = 0; j < N; j++)
        {
			r[i * N + j] = I[i * N + j] - y[i * N + j];
		}
	}
	free(y);
}

void sum(double *m1, double *m2, int N)//сумма двух матриц
{
	for(int i = 0; i < N; i++)
    {
		for(int j = 0; j < N; j++)
        {
			m1[i * N + j] += m2[i * N + j];
		}
	}
}

void inverse(double *a, int N, int M)
{
	double io;
	double* I; // единичная матрица
	double* t; // транспонированная матрица а
	double* b; // t/(maxj * maxi)
	double* r;
	double* c; // обратная матрица
	double* old_matrix;
	double* tmp;

	I=(double*)malloc(sizeof(double)*N*N);
	t=(double*)malloc(sizeof(double)*N*N);
	b=(double*)malloc(sizeof(double)*N*N);
	r=(double*)malloc(sizeof(double)*N*N);
	c=(double*)malloc(sizeof(double)*N*N);
	old_matrix = (double*)malloc(sizeof(double)*N*N);
	tmp = (double*)malloc(sizeof(double)*N*N);

	for(int i = 0; i < N; i++)
	old_matrix[i * N + i] = a[i * N +  i];

	for(int j = 0; j < N; j++){
		I[j*N + j] = 1;
	} // получаем I

	transpose(a, t, N); // получаем t

	io = maxj(a, N)*maxi(a,N);
	divsc(b, t, N, io); // получаем b
	make_r(a,b,I,r,N);
	make_r(a,b,I,t,N);

	for(int i = 1; i <= M; i++){
		sum(I, t, N);
		mult(t, r, a, N);
		memcpy(t, a, sizeof(double)*N*N);
	}

	mult(I, b, c, N);

	mult(c, old_matrix, tmp, N);
	for(int i =0 ; i < N; i++)
	if(abs(tmp[i*N + i] - 1) > 0.2)
	printf("%0.2f %d\nFAIL! " , a[i*N +i] , i);

	free(b);
	free(c);
	free(I);
	free(t);
	free(r);
	free(old_matrix);
	free(tmp);
}

int main(int argc, char* argv[])
{
    struct timespec start, end;
    int N;
    int M;
    double *a;

    if(argc < 3)
    {
        printf("Write size N = ");
        scanf("%d",&N);
        printf("Write row length M = ");
        scanf("%d",&M);
    }
    else
    {
        N = atoi(argv[1]);
        M = atoi(argv[2]);
    }

     a=(double*)malloc(sizeof(double)*N*N);
     matr_mk(N, a);
     clock_gettime(CLOCK_MONOTONIC_RAW, &start);
     inverse(a, N, M);
     clock_gettime(CLOCK_MONOTONIC_RAW, &end);
     printf("Time taken(PURE): %.10lf sec.\n",end.tv_sec-start.tv_sec+ 0.000000001*(end.tv_nsec-start.tv_nsec));
     free(a);

    return 0;
}



