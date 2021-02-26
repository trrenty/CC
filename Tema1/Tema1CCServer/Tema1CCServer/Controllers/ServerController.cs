using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;
using Tema1CCServer.KeyValidator;
using Newtonsoft.Json;
using System.Text;
using Newtonsoft.Json.Linq;
using System.Diagnostics;
using System.IO;

namespace Tema1CCServer.Controllers
{


    [Route("api/test")]
    [ApiController]
    [ApiKey]
    public class ServerController : ControllerBase

    {

        private static string payload = JsonConvert.SerializeObject(new
        {
            jsonrpc = "2.0",
            method = "generateIntegers",
            Params = new
            {
                apiKey = "ff3a0652-0b29-4df9-95e9-8e81743cebf4",
                n = 1,
                min = 0,
                max = 101
            },
            id = 1
        }).Replace('P', 'p');

        private static readonly HttpClient client = new HttpClient();

        private static readonly string RANDOM_ORG_URI = "https://api.random.org/json-rpc/1/invoke";
        private static readonly string IMG_FLIP_URI = "https://api.imgflip.com/get_memes";
        private static readonly string MEME_GEN_URI = "http://apimeme.com/meme?meme={0}&top={1}&bottom={2}";
        private static readonly string JOKE_API_URI = "https://v2.jokeapi.dev/joke/Any";


        private static string[] memes;



        private static readonly Logger _logger = new Logger(Directory.GetCurrentDirectory());

        public ServerController()
        {

            if (memes == null)
            {
                memes = getMemesArray().Result;
            }

        }

        [Route("metrics")]
        public async Task<IActionResult> GetMetrics()
        {
            _logger.log("Get request on /api/test/metrics");
            return Ok(_logger.getLogs());
        }

        [Route("stats")]
        public async Task<IActionResult> GetStatus()
        {
            _logger.log("Get request on /api/test/stats");
            int statusCode1, statusCode2, statusCode3, statusCode4;
            double time1, time2, time3, time4;
            _logger.log("Sending request to RandomOrg");

            Stopwatch sw = new Stopwatch();
            sw.Start();
            statusCode1 = (int)(client.GetAsync(RANDOM_ORG_URI).Result.StatusCode);
            sw.Stop();
            time1 = sw.Elapsed.TotalSeconds;
            sw.Reset();
            _logger.log("Response: " + statusCode1 + " time: " + time1);
            _logger.log("Sending request to IMGFLIP");

            sw.Start();
            statusCode2 = (int)(client.GetAsync(IMG_FLIP_URI).Result.StatusCode);
            sw.Stop();
            time2 = sw.Elapsed.TotalSeconds;
            sw.Reset();
            _logger.log("Response: " + statusCode2 + " time: " + time2);
            _logger.log("Sending request to MEMEGenerator");

            sw.Start();
            statusCode3 = (int)(client.GetAsync(string.Format(MEME_GEN_URI, "10-Guy", "nothing", "nothing")).Result.StatusCode);
            sw.Stop();
            time3 = sw.Elapsed.TotalSeconds;
            sw.Reset();
            _logger.log("Response: " + statusCode3 + " time: " + time3);
            _logger.log("Sending request to JokeApi");

            sw.Start();
            statusCode4 = (int)(client.GetAsync(JOKE_API_URI).Result.StatusCode);
            sw.Stop();
            time4 = sw.Elapsed.TotalSeconds;
            sw.Reset();
            _logger.log("Response: " + statusCode4 + " time: " + time4);


            return Ok(JsonConvert.SerializeObject(new { 
                randomOrg = new
                {
                    status = statusCode1,
                    time = time1
                },
                imgFlip = new
                {
                    status = statusCode2,
                    time = time2
                },
                memeGen = new
                {
                    status = statusCode3,
                    time = time3
                },
                jokeApi = new
                {
                    status = statusCode4,
                    time = time4
                }
            }));

        }


        public async Task<IActionResult> Get() {

            _logger.log("Get request on /api/test");
            var resultNumber = await getRandomNumber();

            var resultMemeTitle = memes[resultNumber];

            var resJoke = await getJoke();

            var memeTexts = resJoke.Split('@');

            var result = getMeme(resultMemeTitle, memeTexts[0], memeTexts[1]);

            return Ok(result);
        }

        private async Task<int> getRandomNumber() {

            //var content = "{\"jsonrpc\": \"2.0\",\"method\": \"generateIntegers\",\"params\": {\"apiKey\" :" +
            //                " \"ff3a0652-0b29-4df9-95e9-8e81743cebf4\",\"n\": 1,\"min\": 0,\"max\": 10 },\"id\" : 1};";

            _logger.log("Sending request to RandomOrg");
            var stringCont = new StringContent(payload, Encoding.UTF8, "application/json");
            var response = await client.PostAsync(RANDOM_ORG_URI, stringCont);
            _logger.log("Response: " + response.StatusCode.ToString());
            var responseString = await response.Content.ReadAsStringAsync();

            var myObj = JObject.Parse(responseString);

            var result = myObj.SelectToken("result").SelectToken("random").SelectToken("data");
            var numberStr =  result.Value<JArray>();
            var str = numberStr.ToString().Trim('[', ' ', '{', ')', ']', '\r', '\n');
            int number = Int32.Parse(str);

            return number;
        }

        public static async Task<string[]> getMemesArray()
        {
            _logger.log("Sending request to IMGFLIP");

            var response = client.GetAsync(IMG_FLIP_URI).Result;
            _logger.log("Response: " + response.StatusCode.ToString());

            var responseString = await response.Content.ReadAsStringAsync();


            var myObj = JObject.Parse(responseString).SelectToken("data").SelectToken("memes").Value<JArray>();


            string[] memes = new string[myObj.Count];
            int i = 0;
            foreach (JObject meme in myObj)
            {
                memes[i++] = meme.GetValue("name").ToString();
            }

            return memes;
        }

        public static async Task<string> getMeme(string meme, string topText = "Top+Text", string bottomText = "Bottom+Text")
        {
            string memeName = meme.Replace(' ', '-').Replace("'", "").Replace(".", "").Replace(",", "%2C");
            string uri = string.Format(MEME_GEN_URI, memeName, topText, bottomText);

            _logger.log("Sending request to MEMEApi");

            var response = client.GetAsync(uri).Result;
            _logger.log("Response: " + response.StatusCode.ToString());

            var responseString = response.Content.ReadAsByteArrayAsync().Result;

            return Convert.ToBase64String(responseString);
        }

        public static async Task<string> getJoke()
        {
            _logger.log("Sending request to JokeApi");

            var response = client.GetAsync(JOKE_API_URI).Result;
            _logger.log("Response: " + response.StatusCode.ToString()) ;

            if (response.IsSuccessStatusCode)
            {
                var responseString = await response.Content.ReadAsStringAsync();

                JObject root = JObject.Parse(responseString);

                string setup = null;
                string delivery = null;
                if (root.GetValue("type").ToString().Equals("twopart"))
                {
                    setup = RemoveBadChars(root.GetValue("setup").ToString());
                    delivery = RemoveBadChars(root.GetValue("delivery").ToString());

                }
                else 
                {
                    delivery = RemoveBadChars(root.GetValue("joke").ToString());
                }



                return setup == null ? " " + "@" + delivery : setup + "@" + delivery;
            }

            return null;
            
        }

        public static string RemoveBadChars(string str)
        {
            var sb = new StringBuilder();

            foreach (var chr in str)
            {
                if (!char.IsPunctuation(chr))
                    sb.Append(chr);
            }

            return sb.ToString();
        }

    }
}
