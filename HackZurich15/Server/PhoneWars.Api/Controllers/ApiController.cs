using MongoRepository;
using PhoneWars.Api.Models;
using System;
using System.Web.Http;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Collections.Generic;
using System.Net.Http;
using System.Net;
using System.Drawing;
using System.IO;
using System.Drawing.Drawing2D;
using System.Net.Http.Headers;

namespace PhoneWars.Api.Controllers
{
    [RoutePrefix("api")]
    public class ApiController : System.Web.Http.ApiController
    {
        private MongoRepository<Player> _playerRepo;
        private MongoRepository<History> _historyRepo;

        public ApiController()
        {
            _playerRepo = new MongoRepository<Player>();
            _historyRepo = new MongoRepository<History>();
        }

        [Route("login")]
        [HttpPost]
        public LoginResponse Login([FromBody]LoginRequest request)
        {
            try
            {
                var player = _playerRepo.FirstOrDefault(x => x.Email == request.Email && x.PasswordHash == GetMd5Hash(request.Password));

                if (player == null)
                {
                    return new LoginResponse { ErrorCode = 1, ErrorMessage = "Ungültige Email oder Passwort." };
                }

                var response = new LoginResponse();
                response.PlayerId = player.Id;
                return response;
            }
            catch (Exception ex)
            {
                return new LoginResponse() { ErrorCode = 2, ErrorMessage = ex.Message };
            }
        }

        [Route("register")]
        [HttpPost]
        public RegisterResponse Register([FromBody]RegisterRequest request)
        {
            try
            {
                if (_playerRepo.Exists(x => x.Email.ToLowerInvariant() == request.Email.ToLowerInvariant()))
                {
                    return new RegisterResponse() { ErrorCode = 1, ErrorMessage = "Die Email wurde bereits verwendet." };
                }

                var player = new Player();

                player.Email = request.Email;
                player.HomeAddress = request.HomeAddress;
                player.WorkAddress = request.WorkAddress;
                player.Phone = request.Phone;
                player.Email = request.Email;
                player.PasswordHash = GetMd5Hash(request.Password);

                var image = Convert.FromBase64String(request.ImageBase64);

                player.Image = ImageToByteArray(Resize(ByteArrayToImage(image), 279));

                player.Nickname = request.Nickname;

                InitPlayer(player);

                player.LocationHistory = new List<PlayerLocation>();

                // insert player
                _playerRepo.Update(player);

                AssignVictim(player);

                RegisterResponse response = new RegisterResponse();

                response.Player = _playerRepo.GetById(player.Id);
                response.Hunter = response.Player.HunterId == null ? null : _playerRepo.GetById(response.Player.HunterId);
                response.Victim = response.Player.VictimId == null ? null : _playerRepo.GetById(response.Player.VictimId);

                return response;
            }
            catch (Exception ex)
            {
                return new RegisterResponse() { ErrorCode = 2, ErrorMessage = ex.Message };
            }
        }


        [Route("getgamedata")]
        [HttpGet]
        public GetGameDataResponse GetGameData(string playerId)
        {
            try
            {
                GetGameDataResponse response = new GetGameDataResponse();

                response.Player = _playerRepo.GetById(playerId);
                response.Hunter = response.Player.HunterId == null ? null : _playerRepo.GetById(response.Player.HunterId);
                response.Victim = response.Player.VictimId == null ? null : _playerRepo.GetById(response.Player.VictimId);

                return response;
            }
            catch (Exception ex)
            {
                return new GetGameDataResponse() { ErrorCode = 2, ErrorMessage = ex.Message };
            }
        }     

        [Route("getgamehistory")]
        [HttpGet]
        public GetGameHistoryResponse GetGameHistory()
        {
            try
            {
                GetGameHistoryResponse response = new GetGameHistoryResponse();

                response.History = new List<HistoryItem>();

                var players = _playerRepo.ToList();
                var histories = _historyRepo.OrderByDescending(x => x.DateUtc).Take(10).ToList();

                foreach (var history in histories)
                {
                    var winner = players.FirstOrDefault(x => x.Id == history.WinningPlayerId);
                    var looser = players.FirstOrDefault(x => x.Id == history.LoosingPlayerId);

                    if (winner != null && looser != null)
                    {
                        response.History.Add(new HistoryItem { Image = history.Image, Message = history.DateUtc.ToString("g") + ": " + winner.Nickname + " killed " + looser.Nickname });
                    }
                }

                return response;
            }
            catch (Exception ex)
            {
                return new GetGameHistoryResponse() { ErrorCode = 2, ErrorMessage = ex.Message };
            }
        }

        [Route("updatelocation")]
        [HttpPost]
        public UpdateLocationResponse UpdateLocation([FromBody]UpdateLocationRequest request)
        {
            try
            {
                UpdateLocationResponse response = new UpdateLocationResponse();

                var player = _playerRepo.GetById(request.PlayerId);

                var location = new PlayerLocation { Lat = request.Lat, Lng = request.Lng, DateUtc = DateTime.UtcNow };

                player.LocationHistory = new List<PlayerLocation>() { location };
                              
                _playerRepo.Update(player);

                response.HunterLocation = player.HunterId == null ? null : _playerRepo.GetById(player.HunterId).LocationHistory.LastOrDefault();
                response.VictimLocation = player.VictimId == null ? null : _playerRepo.GetById(player.VictimId).LocationHistory.LastOrDefault();

                response.IsDead = player.IsDead;

                return response;
            }
            catch (Exception ex)
            {
                return new UpdateLocationResponse() { ErrorCode = 2, ErrorMessage = ex.Message };
            }
        }

        [Route("markkilled")]
        [HttpPost]
        public MarkKilledResponse MarkKilled([FromBody]MarkKilledRequest request)
        {
            try
            {
                var player = _playerRepo.GetById(request.PlayerId);
                var victim = _playerRepo.GetById(player.VictimId);

                if (request.SecretCode.ToLowerInvariant() != victim.SecretCode.ToLowerInvariant())
                {
                    return new MarkKilledResponse { ErrorCode = 1, ErrorMessage = "Der Code stimmt nicht." };
                }

                player.Level++;
                player.VictimId = null;

                victim.HunterId = null;
                victim.IsDead = true;

                if (victim.VictimId != null)
                {
                    var victimsVictim = _playerRepo.GetById(victim.VictimId);
                    victimsVictim.HunterId = null;
                    _playerRepo.Update(victimsVictim);

                    // notify victims victim?
                }

                victim.VictimId = null;

                var image = request.ImageBase64 != null ? Convert.FromBase64String(request.ImageBase64) : null;

                // create history entry
                _historyRepo.Update(new History
                {
                    DateUtc = DateTime.UtcNow,
                    Image = image == null ? null : ImageToByteArray(Resize(ByteArrayToImage(image), 279)),
                    WinningPlayerId = player.Id,
                    LoosingPlayerId = victim.Id
                });

                _playerRepo.Update(player);
                _playerRepo.Update(victim);

                AssignVictim(player);

                return new MarkKilledResponse();
            }
            catch (Exception ex)
            {
                return new MarkKilledResponse() { ErrorCode = 2, ErrorMessage = ex.Message };
            }
        }

        /// <summary>
        /// Resets the player from isdead state
        /// </summary>
        [Route("resetdead")]
        [HttpPost]
        public ResetDeadResponse ResetDead([FromBody]ResetDeadRequest request)
        {

            try
            {
                var player = _playerRepo.GetById(request.PlayerId);

                InitPlayer(player);

                _playerRepo.Update(player);

                AssignVictim(player);

                return new ResetDeadResponse();
            }
            catch (Exception ex)
            {
                return new ResetDeadResponse() { ErrorCode = 2, ErrorMessage = ex.Message };
            }
        }

        [Route("getimage")]
        [HttpGet]
        public HttpResponseMessage GetImage(string playerId, int type = 2)
        {
            var player = _playerRepo.GetById(playerId);

            if (player.Image != null && player.Image.Length > 0)
            {
                var image = ByteArrayToImage(player.Image);
                var thumbnail = Resize(image, 40);
                DrawBitmapWithBorder(type == 0 ? Brushes.Green : type == 1 ? Brushes.Red : Brushes.Black, 3, thumbnail);
                
                HttpResponseMessage result = new HttpResponseMessage(HttpStatusCode.OK);
                result.Content = new ByteArrayContent(ImageToByteArray(thumbnail));
                result.Content.Headers.ContentType = new MediaTypeHeaderValue("image/jpeg");
                return result;
            }

            return null;
        }

        private byte[] ImageToByteArray(Image img)
        {
            MemoryStream ms = new MemoryStream();
            img.Save(ms, System.Drawing.Imaging.ImageFormat.Jpeg);
            return ms.ToArray();
        }

        private Image ByteArrayToImage(byte[] byteArrayIn)
        {
            MemoryStream ms = new MemoryStream(byteArrayIn);
            Image returnImage = Image.FromStream(ms);
            return returnImage;
        }

        private Image Resize(Image srcImage, int newWidth)
        {
            var newHeight = (int)Math.Round(srcImage.Height * ((0.0 + newWidth) / srcImage.Width));

            Bitmap newImage = new Bitmap(newWidth, newHeight);
            using (Graphics gr = Graphics.FromImage(newImage))
            {
                gr.SmoothingMode = SmoothingMode.HighQuality;
                gr.InterpolationMode = InterpolationMode.HighQualityBicubic;
                gr.PixelOffsetMode = PixelOffsetMode.HighQuality;
                gr.DrawImage(srcImage, new Rectangle(0, 0, newWidth, newHeight));
            }
            return newImage;
        }

        private void DrawBitmapWithBorder(Brush brush, int width, Image img)
        {
            Graphics gr = Graphics.FromImage(img);

            gr.DrawLine(new Pen(brush, width), new Point(0, 0), new Point(0, img.Height - 1));
            gr.DrawLine(new Pen(brush, width), new Point(0, 0), new Point(img.Width - 1, 0));
            gr.DrawLine(new Pen(brush, width), new Point(0, img.Height - 1), new Point(img.Width - 1, img.Height - 1));
            gr.DrawLine(new Pen(brush, width), new Point(img.Width - 1, 0), new Point(img.Width - 1, img.Height - 1));
        }

        private void InitPlayer(Player player)
        {
            player.Level = 1;
            player.IsDead = false;
            player.StartedDate = DateTime.UtcNow;
            player.SecretCode = RandomString(5);
        }

        private void AssignVictim(Player player)
        {
            var possibleVictims = _playerRepo.Where(x => x.Id != player.Id && x.Id != player.HunterId && x.HunterId == null).ToList();

            var killedIds = _historyRepo.Where(x => x.WinningPlayerId == player.Id).Select(x => x.LoosingPlayerId).ToList();

            // find victim which hasn't been victim before and has similar level
            var victim = possibleVictims.Where(x => !killedIds.Contains(x.Id)).OrderBy(x => Math.Abs(x.Level - player.Level)).FirstOrDefault();

            if (victim != null)
            {
                player.VictimId = victim.Id;
                player.DeadlineDate = DateTime.UtcNow.AddDays(7);

                victim.HunterId = player.Id;

                _playerRepo.Update(player);
                _playerRepo.Update(victim);
                // notify victim?
            }
        }

        private string GetMd5Hash(string input)
        {
            using (MD5 md5Hash = MD5.Create())
            {
                byte[] data = md5Hash.ComputeHash(Encoding.UTF8.GetBytes(input));

                StringBuilder sBuilder = new StringBuilder();
                for (int i = 0; i < data.Length; i++)
                {
                    sBuilder.Append(data[i].ToString("x2"));
                }

                return sBuilder.ToString();
            }
        }

        private string RandomString(int length)
        {
            const string chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            var random = new Random();
            return new string(Enumerable.Repeat(chars, length)
              .Select(s => s[random.Next(s.Length)]).ToArray());
        }
    }
}
