#include "minic-stdlib.h"

int n; int m;
void main() {
  n = 5; m = n + 1;
  while (n > 0) {
    print_i(n);
     print_c('\n');

    while (m > 3) {
      print_s((char*) "I'm greater than 3!\n");
        while (n > 1) {
          print_s((char*) "hello there");
          n = n-1;
        }

        n = n-1; m = m-1;
       print_i(n);
       print_c('\n');
    }



    n = n-1;
  }
   print_s((char*) "Blast Off.\n");
   print_i(n); print_c('\n');
}
