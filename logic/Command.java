package logic;

public class Command {

  private CommandType type;
  private String args;
  private String arg2;

  public Command (CommandType type, String args) {
    this.type = type;
    this.args = args;
    // if (args != null) {
    //     this.args = args.split(" ");
    // }
  }

  public Command (CommandType type, String args, String arg2){
    this.type = type;
    this.args = args;
    this.arg2 = arg2;
  }

  public CommandType getType() {
    return this.type;
  }

  public String getArgs() {
    return this.args;
  }

  public String[] getAllArgs(){
    String[] allArgs = new String[] {args, arg2};
    return allArgs;
  }
}