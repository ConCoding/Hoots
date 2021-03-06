package hoot.system.Filesystem;

import hoot.system.Logger.LoggerInterface;
import hoot.system.objects.Inject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Base64.Decoder;

public class MediaFileHandler
{
    public final static String mediaPath = "media";

    private final String webrootOnFilesystem;
    private final String contextUriPath;

    @Inject private LoggerInterface logger;

    public MediaFileHandler(String webrootOnFilesystem, String contextUriPath)
    {
        this.webrootOnFilesystem = webrootOnFilesystem;
        this.contextUriPath      = contextUriPath;
    }

    /**
     * Decodes and saves types of media with Base64 in the designated media folder.
     *
     * @param mediaName    is the mediaName with its format.
     * @param relativePath is the relative path without the mediaName.
     * @param base64E      is the media in Base64 encoded.
     */
    public void saveBase64Image(String mediaName, String relativePath, String base64E)
    {
        relativePath = relativePath != null ? relativePath : "";

        if (!relativePath.equals("")) {
            this.createDir(relativePath);
        }

        String[] parts       = base64E.split(",");
        String   imageString = parts[1];

        Decoder decoder   = Base64.getMimeDecoder();
        byte[]  imageByte = decoder.decode(imageString);

        String imagePath = this.getMediaFilePath(relativePath + File.separator + mediaName);

        try {
            Path path = Paths.get(imagePath);
            Files.write(path, imageByte);
        } catch (IOException e) {
            this.logger.logException("Saving of File " + imagePath + " failed: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes the specified media from the media folder.
     *
     * @param relativePath is the relative path to the media with the filename which is about to be deleted.
     */

    public void deleteMedia(String relativePath)
    {
        new File(this.getMediaFilePath(relativePath)).delete();
    }

    /**
     * Get the Url from a specific Image.
     *
     * @param relativePath is the relative Path to the Image.
     * @return The URL for the Image.
     */
    public String getImageUrl(String relativePath)
    {
        return this.contextUriPath + "/" + MediaFileHandler.mediaPath + "/" + relativePath;
    }

    public String getMediaFilePath(String relativePath)
    {
        return this.webrootOnFilesystem + MediaFileHandler.mediaPath + File.separator + relativePath;
    }

    public boolean createDir(String relativePath)
    {
        return new File(this.getMediaFilePath(relativePath)).mkdirs();
    }
}
