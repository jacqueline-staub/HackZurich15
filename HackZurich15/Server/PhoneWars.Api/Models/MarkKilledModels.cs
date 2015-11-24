using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace PhoneWars.Api.Models
{
    public class MarkKilledRequest
    {
        public string PlayerId { get; set; }

        public string SecretCode { get; set; }

        public string ImageBase64 { get; set; }

    }

    public class MarkKilledResponse
    {
        public int ErrorCode { get; set; }

        public string ErrorMessage { get; set; }
    }
}