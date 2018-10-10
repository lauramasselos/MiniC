void main() {
  int n;
  n = 4; {
    char c;
    c = 'c'; {
      struct ident s1;
      char str[7];
      str = "Hello!";
    }
  }
}
// should pass parser
