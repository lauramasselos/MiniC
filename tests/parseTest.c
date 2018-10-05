#include "minic-stdlib.h"

/*
structdecl ::= "struct" IDENT { (vardecl)+ };

vardecl ::= type IDENT ; | type IDENT [ INT_LITERAL ];

fundecl ::= type IDENT ( params) block
*/
// tested; structdecl function works!
struct struct1 {
  // tested; vardecl function works!
  int a;
  char* str[15];
  void main;
  struct s str;
};


struct struct2 {
  // tested; vardecl function works!
  int a;
  char* str[15];
  void main;
  struct no u;
};

int* n;
char* s1[90];

void n (int a, int* b) {

  int* c; void d; char e[5]; struct f g;
}

int fundecl() {
  int n;
  struct test ing[10];
  sizeof (int*) > test_1.test;
}
