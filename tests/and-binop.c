#include "minic-stdlib.h"

int m;
int n;

void main() {
  m = 15;
  n = 16;
  if (m!=n && n == 16) {
    print_s((char*) "m!=n");
  }
}
