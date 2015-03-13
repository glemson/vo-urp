/* 
 * File:   main.cpp
 * Author: laurent
 *
 * Created on 25 août 2009, 18:38
 */


#include <stdlib.h>
#include <stdio.h>
#include <time.h>

// iteration
const int N = 10 * 1000 * 1000;

/*
 * 
 */
int main(int argc, char** argv) {

    struct tm *local;
    time_t t;
    time_t start,end;

    t = time(NULL);
    local = localtime(&t);
    printf("Start : UTC time and date: %s\n", asctime(local));

    start = time(NULL);

    for (int k = 0; k < 100; k += 10) {

        printf("%d%%\n", k);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < 211; j++) {
            }
        }

    }

    printf("100 %%\n");

    end = time(NULL);

    printf("Process used %f seconds.\n", difftime(end, start));

    t = time(NULL);
    local = localtime(&t);
    printf("Stop  : UTC time and date: %s\n", asctime(local));

    return (EXIT_SUCCESS);
}

