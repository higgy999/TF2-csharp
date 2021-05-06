using Jitter;
using Jitter.Collision.Shapes;
using Jitter.Dynamics;
using Jitter.LinearMath;
using System.Numerics;
using static Raylib_cs.Raylib;
using static Raylib_cs.Color;

namespace tf2ray
{
	public class Player
	{
		public RigidBody body;
		public Shape shape;

		public Player(ref World world, JVector position)
		{
			shape = new CylinderShape(10, 5);
			body = new RigidBody(shape);
			world.AddBody(body);
			body.Position = position;
		}

		public void Update()
		{
			//reading the input:
			float horizontalAxis = CrossPlatformInputManager.GetAxis("Horizontal");
			float verticalAxis = CrossPlatformInputManager.GetAxis("Vertical");

			//assuming we only using the single camera:
			var camera = Program.camera;

			//camera forward and right vectors:
			var forward = camera.target;
			var right = camera.transform.right;

			//project forward and right vectors on the horizontal plane (y = 0)
			forward.y = 0f;
			right.y = 0f;
			forward.Normalize();
			right.Normalize();

			//this is the direction in the world space we want to move:
			var desiredMoveDirection = forward * verticalAxis + right * horizontalAxis;

			//now we can apply the movement:
			body.Position = new JVector(desiredMoveDirection * 2 * GetFrameTime());
	}

		public void Render()
		{
			//Draw Player
			Vector3 tmp = Program.ConvertJVector(body.Position);
			tmp.Y -= 5f;

			DrawCylinder(	  tmp, 5, 5, 10, 40, BLUE);
			DrawCylinderWires(tmp, 5, 5, 10, 40, DARKBLUE);
		}
	}
}
