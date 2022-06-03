package de.dagere.kopeme.kieker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.io.BinaryValueSerializer;
import kieker.common.registry.writer.WriterRegistry;
import kieker.common.util.filesystem.FSUtil;
import kieker.monitoring.writer.WriterUtil;
import kieker.monitoring.writer.compression.ICompressionFilter;
import kieker.monitoring.writer.filesystem.AbstractLogStreamHandler;
import kieker.monitoring.writer.filesystem.BinaryLogStreamHandler;

public class BinaryLogStreamHandlerFixed extends AbstractLogStreamHandler {

   private static final Logger LOGGER = LoggerFactory.getLogger(BinaryLogStreamHandler.class);

   private final ByteBuffer buffer;

   /**
    * Create a binary log stream handler.
    *
    * @param flushLogFile flush log file
    * @param bufferSize buffer size
    * @param charset charset, presently not used in binary serialization
    * @param compressionFilter compression filter
    * @param writerRegistry writer registry.
    */
   public BinaryLogStreamHandlerFixed(final Boolean flushLogFile, final Integer bufferSize, final Charset charset, // NOPMD charset not used in binary
         final ICompressionFilter compressionFilter, final WriterRegistry writerRegistry) {
      super(flushLogFile, bufferSize, charset, compressionFilter, writerRegistry);
      this.buffer = ByteBuffer.allocateDirect(bufferSize);
      this.serializer = BinaryValueSerializer.create(this.buffer, writerRegistry);
      this.extension = FSUtil.BINARY_FILE_EXTENSION;
   }

   @Override
   public void serialize(final IMonitoringRecord record, final int id) throws IOException {
      this.requestBufferSpace(4 + 8 + record.getSize());

      this.buffer.putInt(id);
      this.buffer.putLong(record.getLoggingTimestamp());

      record.serialize(this.serializer);
      this.numOfEntries++;
   }

   @Override
   public void close() throws IOException {
      this.buffer.flip();
      try {
         while (this.buffer.hasRemaining()) {
            this.numOfBytes += this.outputChannel.write(this.buffer);
         }
         this.buffer.clear();
      } catch (final IOException e) {
         LOGGER.error("Caught exception while writing to the channel.", e);
         WriterUtil.close(this.outputChannel, LOGGER);
      }

      this.serializedStream.flush();
      super.close();
   }

   /**
    * Request space in the buffer, if necessary flush the buffer.
    *
    * @param bufferSpace requested size
    * @param log
    * @throws IOException
    */
   private void requestBufferSpace(final int bufferSpace) throws IOException {
      if (bufferSpace > this.buffer.remaining()) {
         this.buffer.flip();

         try {
            synchronized (this) {
               while (this.buffer.hasRemaining()) {
                  this.numOfBytes += this.outputChannel.write(this.buffer);
               }
               this.buffer.clear();
            }
         } catch (final IOException e) {
            LOGGER.error("Caught exception while writing to the channel.", e);
            WriterUtil.close(this.outputChannel, LOGGER);
         }

         if (this.flushLogFile) {
            this.serializedStream.flush();
         }
      }
   }

}
