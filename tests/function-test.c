void fun1() {
  int n;
  n = 1; {
    char[1] str;
    str = "Another block";
  }
}
// test to fix infinite loop: variable_initialization scoreboard test returning 124 at last commit
