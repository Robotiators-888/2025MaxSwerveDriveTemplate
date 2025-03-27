package frc.robot.Util;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.DriveSubsystem;

    public class AutoGenerator extends SubsystemBase {
        public static DriveSubsystem Drivetrain = DriveSubsystem.getInstance();
        private static AutoGenerator INSTANCE = null;
  public AutoGenerator() {
    // All other subsystem initialization
    // ...
    

    // Load the RobotConfig from the GUI settings. You should probably
    // store this in your Constants file
    RobotConfig config;
    try{
      config = RobotConfig.fromGUISettings();
    } catch (Exception e) {
      // Handle exception as needed
      e.printStackTrace();
      return;
    }

    // Configure AutoBuilder last
    AutoBuilder.configure(
        Drivetrain::getPose, // Robot pose supplier
        Drivetrain::resetOdometry, // Method to reset odometry (will be called if your auto has a starting pose)
        Drivetrain::getChassisSpeeds, // ChassisSpeeds supplier. MUST BE ROBOT RELATIVE
            (speeds, feedforwards) -> Drivetrain.driveRobotRelative(speeds), // Method that will drive the robot given ROBOT RELATIVE ChassisSpeeds. Also optionally outputs individual module feedforwards
            new PPHolonomicDriveController( // PPHolonomicController is the built in path following controller for holonomic drive trains
                    new PIDConstants(5.0, 0.0, 0.0), // Translation PID constants
                    new PIDConstants(5.0, 0.0, 0.0) // Rotation PID constants
            ),
            config, // The robot configuration
            () -> {
              // Boolean supplier that controls when the path will be mirrored for the red alliance
              // This will flip the path being followed to the red side of the field.
              // THE ORIGIN WILL REMAIN ON THE BLUE SIDE

              var alliance = DriverStation.getAlliance();
              if (alliance.isPresent()) {
                return alliance.get() == DriverStation.Alliance.Red;
              }
              return false;
            },
            Drivetrain // Reference to this subsystem to set requirements
    );
  }
  public static AutoGenerator getInstance() {
    if (INSTANCE == null) {
        INSTANCE = new AutoGenerator();
    }
    return INSTANCE;
  }
}

