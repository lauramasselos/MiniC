struct A {
  int a;
  char c;
  void* p;
};

struct B {
  struct A s;
};

struct C {
  struct B s;
};

void main() {

  struct C s;

  s.s.s.a = 12345;

  print_i(s.s.s.a);

  s.s.s.p = mcmalloc(sizeof(struct C) * 4);

  ((struct C*)s.s.s.p)[3].s.s.a = 67890;
  print_i(((struct C*)s.s.s.p)[3].s.s.a);
}
