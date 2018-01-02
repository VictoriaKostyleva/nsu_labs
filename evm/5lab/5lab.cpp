#include <opencv2/opencv.hpp>
#include <stdio.h>
using namespace cv;
using namespace std;

int main(int argc,char *argv[])
{
  double t = 1,time1,time2,time3,load;
  struct timespec start, end;
  struct timespec start1, end1;
  struct timespec start2, end2;
  struct timespec start3, end3;
  CvCapture *capture = cvCreateCameraCapture(0);//создается поток ввода видеоданных с первой камеры
  if (!capture) return 0;
  while(1)//запускается циклическая обработка потока видеоданных
  {
    clock_gettime(CLOCK_MONOTONIC_RAW, &start1);
	//clock_gettime(CLOCK_MONOTONIC_RAW, &start);

    clock_gettime(CLOCK_MONOTONIC_RAW, &start2);
    IplImage *frame = cvQueryFrame(capture);//извлечение очередного кадра
    clock_gettime(CLOCK_MONOTONIC_RAW, &end2);

	IplImage *image = cvCloneImage(frame);
	cvShowImage("without", image);

    if(!frame) break;
    for (int y=0; y<frame->height - 30; y++)
    {
      uchar *ptr = (uchar*)(frame->imageData +y*frame->widthStep);
      for (int x=0; x<frame->width - 300; x++)
      {
        ptr[3*x] = 128;//128; // Blue
        ptr[3*x+2] = 0;//64; // Red
      }
     //clock_gettime(CLOCK_MONOTONIC_RAW, &end);
      // printf("FPS: %lf frame per sec.\n",t/(end.tv_sec-start.tv_sec + 0.000000001*(end.tv_nsec-start.tv_nsec)));
    }

    //cvFlip(frame,frame,1);//отражение изображения

    clock_gettime(CLOCK_MONOTONIC_RAW, &start3);
    cvShowImage("test", frame);//вывод изображения.....
    clock_gettime(CLOCK_MONOTONIC_RAW, &end3);

    clock_gettime(CLOCK_MONOTONIC_RAW, &end1);

    time1 = end1.tv_sec - start1.tv_sec + 0.000000001*(end1.tv_nsec-start1.tv_nsec);
    time2 = end2.tv_sec - start2.tv_sec + 0.000000001*(end2.tv_nsec-start2.tv_nsec);
    time3 = end3.tv_sec - start3.tv_sec + 0.000000001*(end3.tv_nsec-start3.tv_nsec);
    load = ((time2+time3)/time1)*100;
    cout << "cyc: " << time1 << endl;
   	cout << "ima: " << time2 << endl;
    cout << "out: " << time3 << endl;
    cout << load << " %" << endl;

    char c = cvWaitKey(33);
    if(c == 27) break;//если пользователь нажал Esc, выйти из цикла обработки и показа кадров видеопотока
  }
  cvReleaseCapture(&capture);//удаление потока ввода видеоданных, удаление окна
  cvDestroyWindow("test");

  cout << "end" << endl;
  return 0;
}
