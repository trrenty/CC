using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Tema1CCServer
{
    public class Logger
    {
        private StringBuilder sb;
        private readonly string path;


        public Logger(string path)
        {
            this.path = path;
            sb = new StringBuilder(4096);
        }

        public void log(string info)
        {
            sb.Append(DateTime.Now.ToString());
            sb.Append(info);
            sb.AppendLine();

            if (sb.Length > 4200)
            {
                File.AppendAllText(path + "/log.txt", sb.ToString());
                sb.Clear();
            }
        }

        public string getLogs()
        {
            return sb.ToString();
        }
        }
}
