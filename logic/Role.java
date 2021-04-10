package logic;

public abstract class Role {
    private String roleName;
    private int requiredRank;
    private String roleDescriptor;
    //if not filled = -1, else contains int of player
    private int filled;

    public Role(String name, String level, String quote) {
        roleName=name;
        requiredRank = Integer.parseInt(level);
        roleDescriptor = quote;
        filled = -1;
    }

    public static int getNumRoles(String[] array){
        int k=0;
            for(int i=0; i<array.length; i++){
                if(array[i]!=null){
                    k++;
                }
            }
        return k;
    }

    public int getNumRoles(Role[] array){
        int k=0;
            for(int i=0; i<array.length; i++){
                if(array[i]!=null){
                    k++;
                }
            }
        return k;
    }

    public String getRoleName(){
        return this.roleName;
    }

    public String getRoleDescriptor() {
        return this.roleDescriptor;
    }

    public boolean isFilled(){
        return filled>=0;
    }
    public void fillRole(int player) {
        filled = player;
    }

    public int getRequiredRank() {
        return this.requiredRank;
    }
    
}