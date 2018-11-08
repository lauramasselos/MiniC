#include "minic-stdlib.h"

int m;
int n;

void main() {
  m = 15;
  n = 16;
  while (m!=n && n == 16) {
    print_s((char*) "m!=n");
    m = m+1;
  }
}
