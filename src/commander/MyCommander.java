
package commander;

import java.util.*;

import com.aisandbox.cmd.*;
import com.aisandbox.cmd.info.*;
import com.aisandbox.cmd.cmds.*;
import com.aisandbox.util.*;



public class MyCommander extends SandboxCommander {
    private String myTeam;
    private String enemyTeam;

    private int myTeamSize;

    private Vector2 middle;
    private Vector2 left;
    private Vector2 right;
    private Vector2 front;

    private Vector2 myFlagSpawnLocation;
    private Vector2 enemyFlagSpawnLocation;

    private Vector2 myScoreLocation;
    private Vector2 enemyScoreLocation;

    private BotInfo attacker;
    private BotInfo defender;

    private Map<String, TeamInfo> myTeamInfo;
    private Map<String, BotInfo> bots;

    private ArrayList<BotInfo> attackers;
    private ArrayList<BotInfo> defenders;
    private ArrayList<BotInfo> scouts;
    private ArrayList<BotInfo> avengers;

    private BotInfo[] attackerArray;

    /**
     * Custom commander class construtor.
     */
    public MyCommander() {
        name = "JavaCmd";

        attacker = null;
        defender = null;



    }

    /**
     * Called when the server sends the "initialize" message to the commander.
     * Use this function to setup your bot before the game starts.
     * You can also set this.verbose = true to get more information about each bot visually.
     */
    @Override
    public void initialize() {
        // set the name of our and the enemy teams
        myTeam = gameInfo.getTeam();
        enemyTeam = gameInfo.getEnemyTeam();

        myTeamSize = gameInfo.getMyTeamInfo().getMembers().size();

        attackerArray = new BotInfo[myTeamSize];

        bots = gameInfo.getBots();



        attackers = new ArrayList<BotInfo>();
        defenders = new ArrayList<BotInfo>();
        scouts = new ArrayList<BotInfo>();
        avengers = new ArrayList<BotInfo>();
        // Calculate flag positions and store the middle.
        Vector2 ours = gameInfo.getMyFlagInfo().getPosition();
        Vector2 theirs = gameInfo.getEnemyFlagInfo().getPosition();
        middle = new Vector2((ours.getX() + theirs.getX()) / 2, (ours.getY() + theirs.getY()) / 2);

        //get flag spawn locations
        myFlagSpawnLocation = levelInfo.getFlagSpawnLocations().get(myTeam);
        enemyFlagSpawnLocation = levelInfo.getFlagSpawnLocations().get(enemyTeam);

        //get flag score locations
        myScoreLocation = levelInfo.getFlagScoreLocations().get(myTeam);
        enemyScoreLocation = levelInfo.getFlagScoreLocations().get(enemyTeam);

        // Now figure out the flaking directions, assumed perpendicular.
        Vector2 d = new Vector2(ours);
        d.sub(theirs);
        left = new Vector2(-d.getY(), d.getX()).normalize();
        right = new Vector2(d.getY(), -d.getX()).normalize();
        front = new Vector2(d.getX(), d.getY()).normalize();

        // display the command descriptions next to the bot labels
        verbose = true;
    }

    /**
     * Called when the server sends a "tick" message to the commander.
     * Override this function for your own bots.  Here you can access all the information in this.gameInfo,
     * which includes game information, and this.levelInfo which includes information about the level.
     * You can send commands to your bots using the issue() method in this class.
     */
    @Override
    public void tick() {



        if (attacker != null && attacker.getHealth() <= 0)
            attacker = null; // the attacker is dead we'll pick another when available

        if (defender != null && defender.getHealth() <= 0)
            defender = null; // the defender is dead we'll pick another when available

        for (int i = 0; i < attackerArray.length - 1; i++){
           if(attackerArray[i] != null)
               /*
               so far every time it has checked the bot health, it's 100, so this never gets called, the bots never get removed
                */
            System.out.println("Checking bot health: " + attackerArray[i].getHealth());
            if (attackerArray[i] != null && attackerArray[i].getHealth() <= 0) {
                attackerArray[i] = null;
                System.out.println("attackerArray index " + i + " set to null");
            }
            }

        // loop through all living bots without orders
        for (BotInfo bot : gameInfo.botsAvailable()) {

                /*
                i tried removing the for loop below, which seemed like it would work, but i started getting null pointers because the botsAvailable
                is sometimes a different size than attackerArray and it got angry
                 */
                for(int i = 0; i < attackerArray.length; i++) {
                    if (attackerArray[i] != null){
                    break;
                    }
                if (attackerArray[i] == null || attackerArray[i].equals(bot) || attackerArray[i].hasFlag()){
                    attackerArray[i] = bot;
                    System.out.println("Attacker set at index " + i);
                }
                    if(attackerArray[i].hasFlag())
                    {
                        // tell the flag carrier to run home
                        System.out.println((attackerArray[i].getName()) + " has the flag");
                        Vector2 target = levelInfo.getFlagScoreLocations().get(myTeam);
                        issue(new MoveCmd(attackerArray[i].getName(), target, "running home"));
                    }
                    else
                    {
                        System.out.println("Attacker does not have flag");
                        Vector2 target = gameInfo.getEnemyFlagInfo().getPosition();
                        Vector2 flank = getFlankingPosition(attackerArray[i], target);
                        System.out.println("Set target and flank position to " + target + " and " + flank);
                        if(target.sub(flank).length() > attackerArray[i].getPosition().sub(target).length())  {
                            issue(new AttackCmd(attackerArray[i].getName(), target, null, "attack from flank"));
                            System.out.println(attackerArray[i].getName() + " attacking flag from flank");
                        }
                        else
                        {
                            flank = levelInfo.findNearestFreePosition(flank);
                            issue(new MoveCmd(attackerArray[i].getName(), flank, "running to flank"));
                            System.out.println(attackerArray[i].getName() + " running to flank");
                        }
                    }
                }




            /*TODO
            When adding bots to the lists, need to set the index Object equal to the bot object I'm iterating through!
            ie; the opposite of what the determineRoles() method below does.
             */
            /*
            determineRoles(bot);
            attackerBehavior(bot);
            scoutBehavior(bot);
            defenderBehavior(bot);
            avengerBehavior(bot);
              */
        }
        for (int i = 0; i < attackerArray.length; i ++){

        }
         /*
         this also didn't work -
        if (attackers.size() >= 1)
            System.out.println("checking attackers list size");
        for(int i = 0; i < attackers.size() - 1; i++){
           BotInfo bot = attackers.get(i);

           if (bot.getHealth() <= 0)  {
               System.out.println("removing bot");
               attackers.remove(i);
           }
        }
         */
        /*  Not a solution, but when I started clearing the list here at least it was assigning bots orders and making them do things
         * every pass */
        /*TODO
          Add actual removal from list logic and place it before the gameInfo.botsAvailable loop
         */
        //attackers.clear();
        scouts.clear();
        defenders.clear();
        avengers.clear();


    }

    private Vector2 getFlankingPosition(BotInfo bot, Vector2 target) {
        List<Vector2> options = new ArrayList<Vector2>();
        Vector2 l = levelInfo.findNearestFreePosition(left.scale(16f).add(target));
        if (l != null) options.add(l);
        Vector2 r = levelInfo.findNearestFreePosition(right.scale(16f).add(target));
        if (r != null) options.add(r);

        if (options.size() > 0) {
            Collections.sort(options);
            return options.get(0);
        } else
            return target;
    }

    //check if bot is assigned to a list
    private boolean isBotAssigned(BotInfo bot) {
        String botName = bot.getName();
        if (attackers != null) {
            for(BotInfo bot2 : attackers)
            if(bot2.getName().equals(botName)){
                System.out.println("Bot is assigned");
           return true;
            }
        }
        System.out.println("Bot is not assigned");
        return false;
    }

    private void removeFromList(ArrayList<BotInfo> list, BotInfo bot){
        String checkBotName = bot.getName();
        for (BotInfo botList : list){
             if (botList.getName().equals(checkBotName))
             {
                 System.out.println("Match found for removal");
                 list.remove(bot);
             }
        }
    }

    //add bots to lists
    private void determineRoles(BotInfo bot) {
        if  ((attackers.size() >= 0) && (attackers.size() < (myTeamSize/4)))
        addAttackers(bot);
        else if ((defenders.size() >=0) && (defenders.size() < (myTeamSize /4)))
        addDefenders(bot);
        else if ((scouts.size() >=0) && (scouts.size() < (myTeamSize /4)))
        addScouts(bot);
        else
        addAvengers(bot);
    }

    //adds to attacker list
    private void addAttackers(BotInfo bot) {

        if (!isBotAssigned(bot)) {
            String botName = bot.getName();
           if (botName != null){
            attackers.add (bot);
            System.out.println("attacker " + botName +" added to list");
           }
        }
            else {
        }
    }

    //adds to defender list
    private void addDefenders(BotInfo bot) {
        if (!isBotAssigned(bot)) {
            String botName = bot.getName();
            if (botName != null){
                defenders.add (bot);
                System.out.println("defender " + botName +" added to list");
            }
        }
        else {
        }
    }

    //adds to scout list
    private void addScouts(BotInfo bot) {
        if (!isBotAssigned(bot)) {
            String botName = bot.getName();
            if (botName != null){
                scouts.add (bot);
                System.out.println("scout " + botName +" added to list");
            }
        }
        else {
        }
    }

    //adds to avenger list
    private void addAvengers(BotInfo bot) {
        if (!isBotAssigned(bot))
            avengers.add(bot);
        else {
        }
    }

    private void attackerBehavior(BotInfo bot) {
        System.out.println("attack method called");
        if (attackers.contains(bot)) {
            System.out.println("bot is in attackers list");
            if (bot.hasFlag()) {
                // tell the flag carrier to run home
                Vector2 target = levelInfo.getFlagScoreLocations().get(myTeam);
                issue(new MoveCmd(bot.getName(), target, "running home"));
            } else {
                Vector2 target = gameInfo.getEnemyFlagInfo().getPosition();
                Vector2 flank = getFlankingPosition(bot, target);
                if (target.sub(flank).length() > bot.getPosition().sub(target).length())
                    issue(new AttackCmd(bot.getName(), target, null, "attack from flank"));
                else {
                    flank = levelInfo.findNearestFreePosition(flank);
                    issue(new MoveCmd(bot.getName(), flank, "running to flank"));
                }
            }
        }
    }


    private void defenderBehavior(BotInfo bot) {
        if (defenders.contains(bot)) {
            Vector2 targetPosition = levelInfo.getFlagSpawnLocations().get(myTeam);
            Vector2 targetMin = targetPosition.sub(new Vector2(2f, 2f));
            Vector2 targetMax = targetPosition.add(new Vector2(2f, 2f));
            Vector2 goal = levelInfo.findRandomFreePositionInBox(new Area(targetMin, targetMax));

            if (goal.sub(bot.getPosition()).length() > 8f)
                issue(new ChargeCmd(bot.getName(), goal, "running to defend"));
            else {
                List<FacingDirection> dirs = new ArrayList<FacingDirection>();
                dirs.add(new FacingDirection(left.sub(bot.getPosition()), 1));
                dirs.add(new FacingDirection(middle.sub(bot.getPosition()), 1));
                issue(new DefendCmd(bot.getName(), dirs, "turning to defend"));
        }
        }
    }

    private void scoutBehavior(BotInfo bot) {
        if (scouts.contains(bot)) {
            Vector2 target = levelInfo.findRandomFreePositionInBox(levelInfo.getLevelArea());
            if (target != null)
                issue(new AttackCmd(bot.getName(), target, null, "random patrol"));
        }

    }

    private void avengerBehavior(BotInfo bot) {
        if (avengers.contains(bot)) {
            issue(new AttackCmd(bot.getName(), enemyScoreLocation, null, "Avenging flag"));
        }

    }

    /**
     * Called when the server sends the "shutdown" message to the commander.
     * Use this function to teardown your bot after the game is over.
     */
    @Override
    public void shutdown() {
        System.out.println("<shutdown> message received from server");
    }
}














































