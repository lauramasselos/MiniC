#include "minic-stdlib.h"

int a[10];
//char str[7];
int n;

int main() {
  n = 0;
  while (n < 10) {
    a[n] = n;
    print_i(a[n]);
    
    // print_i(n);
    n = n+1;
  }
  return 0;
}
