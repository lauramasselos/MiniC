#include "minic-stdlib.h"

int multiply(int x, int y){
  return x * y;
}

int main(){

  print_i(multiply(multiply(3, 2), 5));

  return 0;
}
