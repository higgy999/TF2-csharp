using Raylib_cs;
using static Raylib_cs.KeyboardKey;
using static Raylib_cs.Raylib;
using static Raylib_cs.Color;
using System.Numerics;
using Jitter.Collision;
using Jitter;
using Jitter.Collision.Shapes;
using Jitter.Dynamics;
using Jitter.LinearMath;

namespace tf2ray
{
	public class Program
	{
		static CollisionSystem collision;
		static World world;

		static Player player;

		static RigidBody platformBody;
		static Shape platformShape;

		public static Camera3D camera;
		static int windowWidth = 1280;
		static int windowHeight = 720;

		public static void Main()
		{
			InitWindow(windowWidth, windowHeight, "Team Fortress 2 (RayLib)");
			SetTargetFPS(60);

			collision = new CollisionSystemSAP();
			world = new World(collision);

			player = new Player(ref world, new JVector(0, 5, 0));

			platformShape = new BoxShape(100, 1, 100);
			platformBody = new RigidBody(platformShape);
			world.AddBody(platformBody);
			platformBody.AffectedByGravity = false;
			platformBody.IsStatic = true;

			Image ico = LoadImage(System.AppDomain.CurrentDomain.BaseDirectory + "/Resources/ico.png");
			SetWindowIcon(ico);

			camera = new Camera3D(new Vector3(0f, 10f, 0f), new Vector3(0f, 0f, 0f), new Vector3(0f, 1f, 0f), 90f, CameraType.CAMERA_PERSPECTIVE);

			SetCameraMode(camera, CameraMode.CAMERA_FREE);

			while (!WindowShouldClose())
			{
				Update();

				BeginDrawing();
				ClearBackground(RAYWHITE);
				
				BeginMode3D(camera);
				Draw3D();
				EndMode3D();

				DrawUI();
				EndDrawing();
			}

			CloseWindow();
			UnloadImage(ico);
		}

		static void Update()
		{
			world.Step(1.0f / 60.0f, true);
			UpdateCamera(ref camera);
		}

		static void Draw3D()
		{
			player.Render();

			//Draw Platform
			DrawCube(ConvertJVector(platformBody.Position), 100.0f, 1.0f, 100.0f, DARKGRAY);
			DrawCubeWires(ConvertJVector(platformBody.Position), 100.0f, 1.0f, 100.0f, BLACK);

			DrawGrid(200, 1.0f);
		}

		static void DrawUI()
		{
			DrawText("Welcome to the third dimension!", 10, 40, 20, DARKGRAY);
			DrawFPS(10, 10);
		}

		public static Vector3 ConvertJVector(JVector input)
		{
			return new Vector3(input.X, input.Y, input.Z);
		}
	}
}
