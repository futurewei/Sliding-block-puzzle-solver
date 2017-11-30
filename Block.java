/**
 * Created by laiwei on 11/23/17.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Block {
    public static void main(String[] args) {
        Solver solver = new Solver();
        solver.solve("/Users/laiwei/Desktop/47B/Proj5/src/Big.txt", "1 1 139 139");
       // solver.solve("/Users/laiwei/Desktop/47B/Proj5/src/test1.txt", args[0]);
    }
}

class Solver{
    class Box{
        int height;
        int width;
        int row;
        int col;
        public Box(int height, int width, int row, int col) {
            this.height = height;
            this.width = width;
            this.row = row;
            this.col = col;
        }
    }
    ArrayList<Box> configuration = new ArrayList<>();
    int moves[][]={{1,0},{0,1},{0, -1}, {-1, 0}};
    Set<String> seen_before = new HashSet<>();
    ArrayList<String> actions = new ArrayList<>();
    Box Goal;
    int vertical = 0;
    int horizental = 0;


    public  void solve(String path, String goal)
    {
        setup(path);
        String[] goal_loc = goal.split(" ");
        Goal = new Box(Integer.parseInt(goal_loc[0]), Integer.parseInt(goal_loc[1]),
                Integer.parseInt(goal_loc[2]), Integer.parseInt(goal_loc[3]));

        StringBuilder state = new StringBuilder();
        for(int i=0; i<vertical * horizental; i++)
        {
            state.append("0");
        }

        for(Box cur : configuration)
        {
            for(int startR = cur.row; startR < cur.row + cur.height; startR ++)
            {
                for(int startC = cur.col; startC < cur.col + cur.width; startC ++)
                {
                        state.setCharAt(startR*horizental + startC, '1');
                }
            }
        }

        print_confi(state);


        boolean result = DFS(Goal, configuration, configuration.get(0), vertical, horizental, actions, state);
        if(result)
            System.out.println("success");
        for(int i = 0; i < actions.size(); i++)
        {
            System.out.println(actions.get(i));
        }

    }
    
    public void print_confi(StringBuilder state) {
        for(int i = 0; i< vertical; i++)
        {
            for(int j = 0; j<horizental; j++)
            {
                System.out.print(state.charAt(i*horizental+j));
            }
            System.out.println();
        }

    }


    public  void setup(String path)
    {
        try {
            File file = new File(path);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {
                String[] input = line.split(" ");
                if (i == 0)
                {
                    vertical = Integer.parseInt(input[0]);
                    horizental = Integer.parseInt(input[1]);
                }else {
                    Box box = new Box(Integer.parseInt(input[0]), Integer.parseInt(input[1]),
                            Integer.parseInt(input[2]),Integer.parseInt(input[3]));
                    configuration.add(box);
                }
                i++;
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean DFS(Box Goal, ArrayList<Box> curConfi, Box last, int vertical, int horizental,
                       ArrayList<String> actions, StringBuilder state) {

        //System.out.println(last.row+"."+last.col);
        if(seen_before.contains(state.toString())) {
                //System.out.println("seen before");
                return false;
        }
        seen_before.add(state.toString());

        if (Goal.height == last.height && Goal.width == last.width && Goal.row == last.row && Goal.col == last.col)
        {
            return true;
        }
        for (int i = 0; i < curConfi.size(); i++) {
            Box cur = curConfi.get(i);
            Box original = new Box(cur.height, cur.width, cur.row, cur.col);
            //System.out.println(i);
            if (Goal.height == cur.height && Goal.width == cur.width && Goal.row == cur.row && Goal.col == cur.col)
            {
                return true;
            }
            int c_row = cur.row;
            int c_col = cur.col;
            StringBuilder state_copy = new StringBuilder(state);

            for (int d = 0; d < moves.length; d++) {
                    int[] dir = moves[d];
                String action = c_row + " " + c_col + " ";
                cur.row = (c_row + dir[0]);
                cur.col = (c_col +dir[1]);
                action += cur.row + " " + cur.col;

                //System.out.println("attempts"+cur.row+","+cur.col);
                //print_confi(state_copy);
                //if cannot move to this direction.
                if (out_bounds(cur, vertical, horizental, state_copy, original))
                {
                    cur.row = c_row;
                    cur.col = c_col;
                    continue;
                }

               // System.out.println("original:" + c_row+","+c_col +"  now:"+cur.row+","+cur.col);

                //when moving, clear original occupied space
                for(int startR = c_row; startR < c_row + cur.height; startR ++)
                {
                    for(int startC = c_col; startC < c_col + cur.width; startC++)
                    {
                        state_copy.setCharAt(startR*horizental + startC, '0');
                    }
                }

                for(int startR = cur.row; startR < cur.row + cur.height; startR ++)
                {
                    for(int startC = cur.col; startC < cur.col + cur.width; startC ++)
                    {
                        state_copy.setCharAt(startR*horizental + startC, '1');

                    }
                }

              //  print_confi(state_copy);
                actions.add(action);
                curConfi.set(i, cur);
                if (DFS(Goal, curConfi, cur, vertical, horizental, actions, state_copy))
                    return true;
                cur.row = c_row;
                cur.col = c_col;
                //state = state_copy;
                curConfi.set(i, cur);
               // System.out.println("backtrack ");
               // print_confi(state);
                //also need to recover your fucking state!
                actions.remove(actions.size()-1);
            }
        }
        return false;
    }


    public  boolean out_bounds(Box cur, int row, int col, StringBuilder state, Box original)
    {
        //check boundary
        if(cur.row < 0 || cur.row + cur.height > row || cur.col <0 || cur.col+ cur.width > col)
            return true;
        //check overlap with other box!!!!
        for(int startR = cur.row; startR < cur.row + cur.height; startR ++)
        {
            for(int startC = cur.col; startC < cur.col + cur.width; startC ++)
            {
                if(state.charAt(startR*horizental + startC) == '1' 
                        && ! (( startR <= original.row + original.height && startC <= original.col + original.width)
                                && (startR >= original.row && startC >= original.col)))
                    return true;
            }
        }
        return false;
    }
}



