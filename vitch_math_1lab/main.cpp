#include <iostream>
#include <tgmath.h>
using namespace std;

double cube(double x) {
    return x * x * x;
}

double square(double x) {
    return x * x;
}

double f(double x, double a, double b, double c) {
    double x_3 = cube(x);
    double x_2 = square(x);
    return x_3 + a * x_2 + b * x + c;
}

double d_of_f_derivative(double a, double b, double c) {
    double d = 4 * a * a - 12 * b;
    return d;
}

double look_for(double *xi, double *yi, double a, double b, double c, double epsilon) {//checked
    if (f(*xi, a, b, c) == 0)
        return *xi;
    if (f(*yi, a, b, c) == 0)
        return *yi;
    double zi = (*xi + *yi) / 2;

    while ((f(zi, a, b, c)) * (f(zi, a, b, c)) > epsilon * epsilon) {
        if (f(zi, a, b, c) * f(*xi, a, b, c) < 0) {
            //xi не меняется
            *yi = zi;
            zi = look_for(xi, yi, a, b, c, epsilon);
        } else {
            //yi не меняется
            *xi = zi;
            zi = look_for(xi, yi, a, b, c, epsilon);
        }
    }
    return zi;//это и есть корень
}

double go_to_right(double t, double a, double b, double c, double epsilon, double delta) {//f(t)<eps checked
    double count = 0;
    while (f(t, a, b, c) < epsilon) {
        t += delta;
        count++;
    }
    return count;
}

double go_to_left(double t, double a, double b, double c, double epsilon, double delta) {//?+-
    double count = 0;
    while (f(t, a, b, c) > epsilon) {
        t -= delta;
        count++;
    }
    return count;
}

void one(double a, double b, double c, double epsilon, double delta, double zero) {
    double t;
    double left;
    double right;
    double r1;
    if (f(zero, a, b, c) < 0) {
        t = go_to_right(zero, a, b, c, epsilon, delta);
        left = t + zero - 1;//zero = x
        right = t + zero;
    } else {
        t = go_to_left(zero, a, b, c, epsilon, delta);
        left = -t + zero;
        right = -t + zero + 1;
    }

    r1 = look_for(&left, &right, a, b, c, epsilon);
    cout << "one. x1= " << r1 << endl;
    return;

}

int main() {
    double epsilon = 0;
    double delta = 0;
    double a = 0;
    double b = 0;
    double c = 0;
    double zero = 0;

    cout << "Enter parametres of equation x^3 + ax^2 + bx + c = 0" << endl;
    cout << "a = ";
    cin >> a;
    cout << "b = ";
    cin >> b;
    cout << "c = ";
    cin >> c;
    cout << "Enter epsilon and step delta" << endl;
    cout << "epsilon = ";
    cin >> epsilon;
    cout << "delta = ";
    cin >> delta;

    double r1 = 0;
    double r2 = 0;
    double r3 = 0;

    if ((d_of_f_derivative(a, b, c) <= epsilon)) {//1
        one(a, b, c, epsilon, delta, zero);
    }
    if ((d_of_f_derivative(a, b, c) > epsilon)) {
        double z1 = (-a + sqrt(a * a - 3 * b)) / 3;
        double z2 = (-a - sqrt(a * a - 3 * b)) / 3;

        if (f(z1, a, b, c) * f(z2, a, b, c) > epsilon) {//1
            one(a, b, c, epsilon, delta, zero);

        }
        else {

            double z2 = (-a + sqrt(a * a - 3 * b)) / 3;
            double z1 = (-a - sqrt(a * a - 3 * b)) / 3;
//            cout << " z1 = " << z1 << endl;
//            cout << " z2 = " << z2 << endl;

            if(f(z1, a, b, c)*f(z2, a, b, c) < -epsilon) {

                r2 = look_for(&z1, &z2, a, b, c, epsilon);
                cout << "three. r2 = " << r2 << endl;

                double t;
                double left;
                double right;
                t = go_to_left(z1, a, b, c, epsilon, delta);
                left = -t + z1;
                right = -t + z1 + 1;
                r1 = look_for(&left, &right, a, b, c, epsilon);
                cout << "three. r1 = " << r1 << endl;

                t = go_to_right(z2, a, b, c, epsilon, delta);
                left = t + z2 - 1;//zero = x
                right = t + z2;
                r3 = look_for(&left, &right, a, b, c, epsilon);
                cout << "three. r3 = " << r3 << endl;
                return 0;

            }
            else {//<=eps^2
                if(square(f(z1, a, b, c)) < epsilon*epsilon ) {
                    r1 = z1;
                    double t = go_to_right(z1, a, b, c, epsilon, delta);
                    double left;
                    double right;
                    //
                    left = t + z1 - 1;//zero = x
                    right = t + z1;

//                    cout << "left = " << left<< endl;
//                    cout << "right = " << right<< endl;

                    r2 = look_for(&left, &right, a, b, c, epsilon);
                    cout << "two. r1 (double) = " << r1<< endl;
                    cout << "two. r2 = " << r2<< endl;
                    return 0;

                }
                if(square(f(z2, a, b, c)) < epsilon*epsilon ) {
                    r2 = z2;
                    r1 = go_to_left(z2, a, b, c, epsilon, delta);

                    //
                    double t;
                    double left;
                    double right;
                    t = go_to_left(z2, a, b, c, epsilon, delta);
                    left = -t + z2;
                    right = -t + z2 + 1 - 0.01;
                    r1 = look_for(&left, &right, a, b, c, epsilon);
                    cout << "two. r1 = " << r1 << endl;
                    cout << "two. r2 (double)= " << r2 << endl;

                }
            }
        }
    }
    return 0;
}