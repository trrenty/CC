using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Tema1CCServer.Controllers;

namespace Tema1CCServer
{
    public class Testing
    {
        public static void Main2(string[] args)
        {
            var dick = ServerController.getMeme("Brace Yourselves X is Coming");
            Console.WriteLine(dick);
        }
    }
}
