using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace PhoneWars.Api.Models
{
    public class RegisterRequest
    {
        public string ImageBase64 { get; set; }

        public string Nickname { get; set; }

        public string HomeAddress { get; set; }

        public string WorkAddress { get; set; }

        public string Phone { get; set; }

        public string Email { get; set; }

        public string Password { get; set; }
    }

    public class RegisterResponse: GetGameDataResponse
    {
        

    }
}