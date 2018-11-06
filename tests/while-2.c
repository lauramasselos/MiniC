#include "minic-stdlib.h"

int n;
void main() {
  n = 5;
  while (n > 0) {
    print_i(n);
    print_c('\n');
    n = n - 1;
  }
  print_s((char*) "Blast Off.\n");
  print_i(n); print_c('\n');
  while (n < 5) {
    print_s((char*) "I'm less than 5.\n");
    n = n+1;
  }
}
