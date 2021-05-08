using System;
using GLFW;
using static OpenGL.GL;

class Program
{
	static void Main(string[] args)
	{
        if (!Glfw.Init())
            Environment.Exit(-1);

        // Create a windowed mode window and its OpenGL context
        var window = Glfw.CreateWindow(1280, 720, "Team Fortress 2 (C#)", Monitor.None, Window.None);
        if (window == Window.None)
        {
            Glfw.Terminate();
            Environment.Exit(-1);
        }

        Glfw.WindowHint(Hint.ClientApi, ClientApi.OpenGL);
        Glfw.WindowHint(Hint.ContextVersionMajor, 3);
        Glfw.WindowHint(Hint.ContextVersionMinor, 3);
        Glfw.WindowHint(Hint.OpenglProfile, Profile.Core);
        Glfw.WindowHint(Hint.Doublebuffer, true);
        Glfw.WindowHint(Hint.Decorated, true);

        Glfw.MakeContextCurrent(window);
        Import(Glfw.GetProcAddress);

        // Make the window's context current
        Glfw.MakeContextCurrent(window);

        // Loop until the user closes the window
        while (!Glfw.WindowShouldClose(window))
        {
            // CLear Collors and 
            glClearColor(0, 0, 0, 1);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Swap front and back buffers
            Glfw.SwapBuffers(window);

            // Poll for and process events
            Glfw.PollEvents();
        }

        Glfw.Terminate();
    }

    void Update()
	{

	}

    void Render()
	{

	}
}
