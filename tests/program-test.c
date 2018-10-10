// full program test
#include "minic-stdlib.h"
#include "another_library.h"

struct identStruct {
  int n1;
  int array[10];
};

struct anotherStruct {
  char c;
  char string[10];
};

int a;
int* point;
struct s1* s2;
struct s3 s4[15];
struct s5* s6[1];

void fun1() {
  int n;
  n = 1; {
    char str[14];
    str = "Another block";
  }
  while (n < 5)
    if ((int) c != n) return 0;
    else {
      fun1();
    }
}

int fun2(int a, int* point, char string) {
  if (5) {
    while (*point > a) return;
  }
  return 0;
}
// should pass parser
