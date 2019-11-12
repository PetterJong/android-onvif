//package com.wp.android_onvif.util;
//
//public class WsseHelper
//{
//    /// <summary>
//    /// 获取加密的字节数组
//    /// </summary>
//    /// <param name="nonce"></param>
//    /// <param name="createdString"></param>
//    /// <param name="basedPassword"></param>
//    /// <returns></returns>
//    private static byte[] buildBytes(String nonce, String createdString, String basedPassword)
//    {
//        byte[] nonceBytes = System.Convert.FromBase64String(nonce);
//        byte[] time = Encoding.UTF8.GetBytes(createdString);
//        byte[] pwd = Encoding.UTF8.GetBytes(basedPassword);
//
//        byte[] operand = new byte[nonceBytes.Length + time.Length + pwd.Length];
//        Array.Copy(nonceBytes, operand, nonceBytes.Length);
//        Array.Copy(time, 0, operand, nonceBytes.Length, time.Length);
//        Array.Copy(pwd, 0, operand, nonceBytes.Length + time.Length, pwd.Length);
//
//        return operand;
//    }
//
//    /// <summary>
//    /// 计算指定字节数组的哈希值。
//    /// </summary>
//    /// <param name="data"></param>
//    /// <returns></returns>
//    public static byte[] SHAOneHash(byte[] data)
//    {
//        using (SHA1Managed sha1 = new SHA1Managed())
//        {
//            var hash = sha1.ComputeHash(data);
//            return hash;
//        }
//    }
//
//    /// <summary>
//    /// 获取加密后的字符串
//    /// </summary>
//    /// <param name="nonce"></param>
//    /// <param name="createdString"></param>
//    /// <param name="password"></param>
//    /// <returns></returns>
//    public static String GetPasswordDigest(String nonce, String createdString, String password)
//    {
//        byte[] combined = buildBytes(nonce, createdString, password);
//        String output = System.Convert.ToBase64String(SHAOneHash(combined));
//        return output;
//    }
//
//    /// <summary>
//    /// 加密的时间戳
//    /// </summary>
//    /// <returns></returns>
//    public static String GetCreated()
//    {
//        return DateTime.Now.ToString("yyyy-MM-ddThh:mm:ssZ");
//    }
//
//    /// <summary>
//    /// 将随机的16位字节数据加密成nonce
//    /// </summary>
//    /// <param name="nonce"></param>
//    /// <returns></returns>
//    public static String GetNonce()
//    {
//        byte[] nonce = new byte[16];
//        new Random().NextBytes(nonce);
//        return System.Convert.ToBase64String(nonce);
//    }
//
//    /// <summary>
//    /// 获取加密后的头部
//    /// </summary>
//    /// <param name="username"></param>
//    /// <param name="password"></param>
//    /// <returns></returns>
//    public static String GetHead(String username, String password)
//    {
//        String res = String.Empty;
//        String format = @"
//            <s:Header>
//    <wsse:Security xmlns:wsse=""http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"" xmlns:wsu=""http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"">
//      <wsse:UsernameToken>
//        <wsse:Username>{0}</wsse:Username>
//        <wsse:Password Type=""http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest"">{1}</wsse:Password>
//        <wsse:Nonce>{2}</wsse:Nonce>
//        <wsu:Created>{3}</wsu:Created>
//      </wsse:UsernameToken>
//    </wsse:Security>
//  </s:Header>
//            ";
//        String nonce = GetNonce();
//        String created = GetCreated();
//        String pas = GetPasswordDigest(nonce, created, password);
//        res = String.format(format, username, pas, nonce, created);
//        return res;
//    }
//}