#include "minic-stdlib.h"

int n;

void main() {
  n = 5;
  if (n == 5) {
    int m;
    m = -3;
    print_i(m);
  }
  else {
    print_s((char*) "This is false.");
  }
}
